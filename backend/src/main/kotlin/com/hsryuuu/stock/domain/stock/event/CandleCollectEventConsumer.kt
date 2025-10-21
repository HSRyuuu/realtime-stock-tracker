package com.hsryuuu.stock.domain.stock.event

import com.hsryuuu.stock.domain.stock.service.CandleStatusManager
import com.hsryuuu.stock.infra.stockapi.service.CandleCollector
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class CandleCollectEventConsumer(
    private val candleCollector: CandleCollector,
    private val candleStatusManager: CandleStatusManager,
    private val indicatorCalcEventConsumer: IndicatorCalcEventConsumer
) {

    private val log = LoggerFactory.getLogger(CandleCollectEventConsumer::class.java)

    /**
     * 캔들 수집 이벤트 처리
     *
     * - 외부 API(TwelveData 등)로부터 데이터 수집 및 DB 저장
     * - 성공 시: Redis 상태 SUCCESS
     * - 실패 시: Redis 상태 FAILED
     */
    @KafkaListener(
        topics = ["candle-collect-topic"],
        groupId = "candle-collector"
    )
    fun handleCandleCollectEvent(event: CandleCollectEvent) {
        log.info("📩[Kafka] 캔들 수집 이벤트 CONSUME: symbol={}, timeframe={}", event.symbol, event.timeframe)
        try {
            // 상태: RUNNING
            candleStatusManager.setRunning(event.symbol, event.timeframe)

            // 실제 수집 로직 실행 (외부 API 조회 + DB 저장)
            candleCollector.collectAndSaveCandles(event.symbol, event.timeframe)

            // 상태: SUCCESS
            log.info("✅ 캔들 데이터 수집 완료: symbol={}, timeframe={}", event.symbol, event.timeframe)
            candleStatusManager.setSuccess(event.symbol, event.timeframe)

            // 볼린저밴드 계산
            indicatorCalcEventConsumer.calcBollingerBand(BollingerBandCalculateEvent(event.symbol, event.timeframe))

        } catch (e: Exception) {
            log.error(
                "❌ 캔들 데이터 수집 실패: symbol={}, timeframe={}, error={}",
                event.symbol, event.timeframe, e.message, e
            )
            // 상태: FAILED
            candleStatusManager.setFailed(event.symbol, event.timeframe, e.message.toString())

            throw e
        }
    }
}