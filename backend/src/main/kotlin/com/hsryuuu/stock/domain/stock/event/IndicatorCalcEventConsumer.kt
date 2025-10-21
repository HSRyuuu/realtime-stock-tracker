package com.hsryuuu.stock.domain.stock.event

import com.hsryuuu.stock.domain.stock.service.IndicatorCalculator
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class IndicatorCalcEventConsumer(
    private val indicatorCalculator: IndicatorCalculator
) {
    private val log = LoggerFactory.getLogger(IndicatorCalcEventConsumer::class.java)

    @KafkaListener(
        topics = ["bollinger-calc-topic"],
        groupId = "bollinger-calc"
    )
    fun calcBollingerBand(event: BollingerBandCalculateEvent) {
        log.info("📩[Kafka] 볼린저밴드 계산 CONSUME: symbol={}, timeframe={}", event.symbol, event.timeframe)
        val bollingerBands =
            indicatorCalculator.calcBollingerBand(event.symbol, event.timeframe, event.period, event.kValue)
        log.info("✅볼린저밴드 계산 완료: size: ${bollingerBands.size}")
    }

    @KafkaListener(
        topics = ["rsi-calc-topic"],
        groupId = "rsi-calc"
    )
    fun calcRSI(event: RSICalculateEvent) {
        log.info("📩[Kafka] RSI 계산 CONSUME: symbol={}, timeframe={}", event.symbol, event.timeframe)
        val rsiList =
            indicatorCalculator.calcRSI(event.symbol, event.timeframe, event.period)
        log.info("✅RSI 계산 완료: size: ${rsiList.size}")
    }

}