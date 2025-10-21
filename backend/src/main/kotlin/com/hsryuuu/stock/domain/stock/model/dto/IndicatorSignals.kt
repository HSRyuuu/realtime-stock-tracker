package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.application.type.IndicatorSignalType

class IndicatorSignals {

    data class BollingerBand(
        val ready: Boolean,
        val signalType: IndicatorSignalType? = null,
        val currentPrice: Double? = 0.0,
        val upper: Double? = 0.0,
        val lower: Double? = 0.0,
        val middle: Double? = 0.0
    )

    data class RSI(
        val ready: Boolean,
        val signalType: IndicatorSignalType? = null,
        val rsi: Double? = 0.0,
        val avgGain: Double = 0.0,
        val avgLoss: Double = 0.0,
        val period: Int = 0
    )
}