package com.hsryuuu.stock.domain.stock.util

import com.hsryuuu.stock.application.type.IndicatorSignalType
import com.hsryuuu.stock.domain.stock.model.dto.IndicatorSignals
import com.hsryuuu.stock.domain.stock.model.dto.IndicatorValues
import org.slf4j.LoggerFactory
import kotlin.math.pow
import kotlin.math.sqrt

object IndicatorUtils {

    private val log = LoggerFactory.getLogger(IndicatorUtils::class.java)


    fun calculateBollingerBand(
        period: Int = 20,
        kValue: Double = 2.00,
        closes: List<Double>,
    ): IndicatorValues.BollingerBand? {
        if (closes.size < period) {
            log.warn("Not enough data for Bollinger Band calculation: ${closes.size}/20")
            return null
        }

        val mean = closes.average()
        val stdDev = sqrt(closes.map { (it - mean).pow(2) }.average())

        val upper = mean + 2 * stdDev
        val lower = mean - 2 * stdDev
        return IndicatorValues.BollingerBand(upper, mean, lower, stdDev, period, kValue)
    }

    fun getBollingerBandCurrentPosition(
        currentPrice: Double,
        upper: Double,
        lower: Double,
        middle: Double
    ): IndicatorSignals.BollingerBand {
        val positionPercent = ((currentPrice - lower) / (upper - lower)) * 100
        val signalType = IndicatorSignalType.fromBollingerBand(positionPercent)
        return IndicatorSignals.BollingerBand(
            ready = true,
            signalType,
            currentPrice,
            upper,
            lower,
            middle
        )
    }

    fun getRSICurrentPosition(
        rsi: Double,
        avgGain: Double,
        avgLoss: Double,
        period: Int
    ): IndicatorSignals.RSI {
        val signalType = IndicatorSignalType.fromRsi(rsi)
        return IndicatorSignals.RSI(
            ready = true,
            signalType,
            rsi,
            avgGain,
            avgLoss,
            period
        )

    }
}