package com.hsryuuu.stock.domain.stock.event

import com.hsryuuu.stock.domain.stock.model.entity.BollingerBand
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.util.IndicatorUtils.calculateBollingerBand
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate

@Component
class IndicatorCalcEventConsumer(
    private val candleRepository: CustomStockCandleRepository,

    ) {

    private val log = LoggerFactory.getLogger(IndicatorCalcEventConsumer::class.java)

    @KafkaListener(
        topics = ["bollinger-calc-topic"],
        groupId = "bollinger-calc"
    )

    fun calcBollingerBand(event: BollingerBandCalculateEvent) {
        log.info("📩[Kafka] 볼린저밴드 계산 CONSUME: symbol={}, timeframe={}", event.symbol, event.timeframe)
        // 볼린저밴드 계산에 필요한 캔들 조회
        val lastBollingerBandDate = candleRepository.findLatestBollingerBandDate(event.symbol, event.timeframe)
        val candles = if (lastBollingerBandDate == null) {
            candleRepository.findCandlesForBollingerBands(event.symbol, event.timeframe, LocalDate.MIN, event.period)
        } else {
            candleRepository.findCandlesForBollingerBands(
                event.symbol,
                event.timeframe,
                lastBollingerBandDate,
                event.period
            )
        }
        val candleCloses = candles.map { it.close.toDouble() }.toList()

        if (candleCloses.size < event.period) {
            log.info("[KAFKA - calcBollingerBand] Candle 건수가 {} 건 이하, ", event.period)
            return
        }

        // 볼린저밴드 계산
        val bollingerBands: ArrayList<BollingerBand> = ArrayList();
        val period = event.period
        for (i in (period - 1) until candleCloses.size) {
            val window = candleCloses.subList(i - period + 1, i + 1)
            val currentCandle = candles[i]
            val bollingerBandData = calculateBollingerBand(period, event.kValue, window) ?: continue

            val bollingerBand = BollingerBand(
                symbol = event.symbol,
                timeframe = event.timeframe,
                bucketStartUtc = currentCandle.bucketStartUtc,
                middle = BigDecimal.valueOf(bollingerBandData.middle),
                upper = BigDecimal.valueOf(bollingerBandData.upper),
                lower = BigDecimal.valueOf(bollingerBandData.lower),
                standardDeviation = BigDecimal.valueOf(bollingerBandData.standardDeviation),
                date = currentCandle.date,
                period = event.period,
                kValue = BigDecimal.valueOf(event.kValue),
            )
            bollingerBands.add(bollingerBand)
        }
        candleRepository.saveAllBollingerBands(bollingerBands)
    }

}