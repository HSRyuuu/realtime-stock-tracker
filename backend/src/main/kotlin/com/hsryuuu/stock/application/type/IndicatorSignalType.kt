package com.hsryuuu.stock.application.type

enum class IndicatorSignalType(
    val description: String
) {
    BUY("매수"),
    NEUTRAL("중립"),
    SELL("매도");

    companion object {

        fun fromBollingerBand(positionPercent: Double): IndicatorSignalType {
            return when {
                positionPercent <= 30 -> BUY
                positionPercent >= 70 -> SELL
                else -> NEUTRAL
            }
        }
    }
}