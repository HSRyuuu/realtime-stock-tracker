package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.model.entity.BollingerBand
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.util.IndicatorUtils.calculateBollingerBand
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Component
class IndicatorCalculator(
    private val candleRepository: CustomStockCandleRepository,
) {
    private val log = LoggerFactory.getLogger(IndicatorCalculator::class.java)

    @Transactional
    fun calcBollingerBand(symbol: String, timeframe: Timeframe, period: Int, kValue: Double): List<BollingerBand> {
        // 볼린저밴드 계산에 필요한 캔들 조회
        val lastBollingerBandDate = candleRepository.findLatestBollingerBandDate(symbol, timeframe)
        val candles = if (lastBollingerBandDate == null) {
            candleRepository.findCandlesForBollingerBands(symbol, timeframe, LocalDate.MIN, period)
        } else {
            candleRepository.findCandlesForBollingerBands(
                symbol,
                timeframe,
                lastBollingerBandDate,
                period
            )
        }
        val candleCloses = candles.map { it.close.toDouble() }.toList()

        if (candleCloses.size < period) {
            log.info("[IndicatorCalculator.calcBollingerBand] Candle 건수가 {} 건 이하, ", period)
            return emptyList()
        }

        // 볼린저밴드 계산
        val bollingerBands: ArrayList<BollingerBand> = ArrayList();
        for (i in (period - 1) until candleCloses.size) {
            val window = candleCloses.subList(i - period + 1, i + 1)
            val currentCandle = candles[i]
            val bollingerBandData = calculateBollingerBand(period, kValue, window) ?: continue

            val bollingerBand = BollingerBand(
                symbol = symbol,
                timeframe = timeframe,
                bucketStartUtc = currentCandle.bucketStartUtc,
                middle = BigDecimal.valueOf(bollingerBandData.middle),
                upper = BigDecimal.valueOf(bollingerBandData.upper),
                lower = BigDecimal.valueOf(bollingerBandData.lower),
                standardDeviation = BigDecimal.valueOf(bollingerBandData.standardDeviation),
                date = currentCandle.date,
                period = period,
                kValue = BigDecimal.valueOf(kValue),
            )
            bollingerBands.add(bollingerBand)
        }
        return candleRepository.saveAllBollingerBands(bollingerBands)
    }
}