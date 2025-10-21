package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.model.entity.BollingerBand
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.entity.StockRsi
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
        val candles = findCandlesForCalculate(symbol, timeframe, period, lastBollingerBandDate)
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

    @Transactional
    fun calcRSI(symbol: String, timeframe: Timeframe, period: Int): List<StockRsi> {
        // 1. 최신 RSI가 계산된 날짜 이후의 캔들 조회
        val lastRSIDate = candleRepository.findLatestRSIDate(symbol, timeframe)
        val candles = findCandlesForCalculate(symbol, timeframe, period, lastRSIDate)

        // 2. 캔들이 부족하면 종료
        if (candles.size <= period) {
            log.info("[IndicatorCalculator.calcRSI] Candle 건수가 부족: ${candles.size}/$period")
            return emptyList()
        }

        // 3. RSI 계산용 종가 리스트 (오래된 → 최신 순)
        val closes = candles.map { it.close.toDouble() }

        // 4. RSI 누적 계산 (EMA 기반)
        val results = mutableListOf<StockRsi>()
        var avgGain = 0.0
        var avgLoss = 0.0

        // 초기 평균 계산 (단순 평균으로 시작)
        val changes = closes.zipWithNext { prev, curr -> curr - prev }
        val gains = changes.map { maxOf(it, 0.0) }
        val losses = changes.map { maxOf(-it, 0.0) }

        avgGain = gains.take(period).average()
        avgLoss = losses.take(period).average()

        // 5. period번째 이후부터 EMA 방식으로 갱신
        for (i in period until closes.size) {
            val gain = gains[i - 1]
            val loss = losses[i - 1]

            avgGain = ((avgGain * (period - 1)) + gain) / period
            avgLoss = ((avgLoss * (period - 1)) + loss) / period

            val rs = if (avgLoss == 0.0) Double.POSITIVE_INFINITY else avgGain / avgLoss
            val rsi = 100 - (100 / (1 + rs))

            val candle = candles[i] // 현재 캔들
            results.add(
                StockRsi(
                    symbol = symbol,
                    timeframe = timeframe,
                    bucketStartUtc = candle.bucketStartUtc,
                    rsi = BigDecimal.valueOf(rsi),
                    avgGain = BigDecimal.valueOf(avgGain),
                    avgLoss = BigDecimal.valueOf(avgLoss),
                    period = period,
                    date = candle.date
                )
            )
        }

        // 6. 저장 및 반환
        return candleRepository.saveAllRsi(results)
    }

    private fun findCandlesForCalculate(
        symbol: String,
        timeframe: Timeframe,
        period: Int,
        refDate: LocalDate?
    ): List<StockCandle> {
        return if (refDate == null) {
            candleRepository.findCandlesToCalcIndicators(symbol, timeframe, LocalDate.MIN, period)
        } else {
            candleRepository.findCandlesToCalcIndicators(
                symbol,
                timeframe,
                refDate,
                period
            )
        }
    }
}