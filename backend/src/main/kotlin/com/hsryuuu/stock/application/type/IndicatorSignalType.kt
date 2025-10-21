package com.hsryuuu.stock.application.type

enum class IndicatorSignalType(
    val description: String
) {
    BEARISH("약세"),
    BULLISH("강세"),
    STRONG_BUY("강한 매수"),
    BUY("매수"),
    NEUTRAL("중립"),
    SELL("매도"),
    STRONG_SELL("강한 매도");

    companion object {

        fun fromBollingerBand(positionPercent: Double): IndicatorSignalType {
            return when {
                positionPercent <= 30 -> BUY
                positionPercent >= 70 -> SELL
                else -> NEUTRAL
            }
        }

        fun fromRsi(rsi: Double): IndicatorSignalType {
            return when {
                rsi < 30 -> BUY
                rsi >= 30 && rsi < 50 -> BEARISH
                rsi >= 50 && rsi < 70 -> BULLISH
                rsi >= 70 -> SELL
                else -> NEUTRAL
            }
        }
    }
}