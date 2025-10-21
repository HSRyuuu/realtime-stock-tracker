package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.application.type.IndicatorSignalType

data class BollingerBandSignal(
    val ready: Boolean,
    val signalType: IndicatorSignalType? = null,
    val currentPrice: Double? = 0.0,
    val upper: Double? = 0.0,
    val lower: Double? = 0.0,
    val middle: Double? = 0.0
) {
}