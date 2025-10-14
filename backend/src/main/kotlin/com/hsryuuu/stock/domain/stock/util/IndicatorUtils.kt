package com.hsryuuu.stock.domain.stock.util

import com.hsryuuu.stock.domain.stock.model.dto.IndicatorValues
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class IndicatorUtils {

    private val log = LoggerFactory.getLogger(IndicatorUtils::class.java)

    fun calculateBollingerBand(
        period: Int = 20,
        kValue: Double = 2.00,
        candles: List<StockCandle>,
    ): IndicatorValues.BollingerBand? {
        if (candles.size < period) {
            log.warn("Not enough data for Bollinger Band calculation: ${candles.size}/20")
            return null
        }

        val closes = candles.map { it.close.toDouble() }
        val mean = closes.average()
        val stdDev = sqrt(closes.map { (it - mean).pow(2) }.average())

        val upper = mean + 2 * stdDev
        val lower = mean - 2 * stdDev
        return IndicatorValues.BollingerBand(upper, mean, lower, stdDev, period, kValue)
    }

    fun calculateRSI(
        candles: List<StockCandle>,
        period: Int = 14
    ): IndicatorValues.RSI? {
        if (candles.size < period) {
            log.warn("Not enough data for RSI calculation: ${candles.size}/$period")
            return null
        }

        val closes = candles.map { it.close.toDouble() }

        // 1. 일별 변화량 계산
        val changes = closes.zipWithNext { prev, curr -> curr - prev }

        // 2. 상승/하락분 분리
        val gains = changes.map { if (it > 0) it else 0.0 }
        val losses = changes.map { if (it < 0) abs(it) else 0.0 }

        // 3. 최근 N일 평균 상승폭/하락폭 계산
        val recentGains = gains.takeLast(period)
        val recentLosses = losses.takeLast(period)
        val avgGain = recentGains.average()
        val avgLoss = recentLosses.average()

        // 4. RS, RSI 계산
        val rs = if (avgLoss == 0.0) Double.POSITIVE_INFINITY else avgGain / avgLoss
        val rsi = 100 - (100 / (1 + rs))

        return IndicatorValues.RSI(
            rsi = rsi,
            avgGain = avgGain,
            avgLoss = avgLoss,
            period = period
        )
    }
}