package com.hsryuuu.stock.application.type

enum class IndicatorSignalType(
    val description: String
) {
    // 📈 공통 추세 관련
    BEARISH("약세 신호"),
    BULLISH("강세 신호"),
    NEUTRAL("중립"),

    // 💡 Bollinger Band 전용 (가격 평가 기반)
    BUY_SIGNAL("매수 신호"),
    SELL_SIGNAL("매도 신호");

    companion object {

        /**
         * Bollinger Band 기준:
         * - 하단(30% 이하): 매수 신호
         * - 상단(70% 이상): 매도 신호
         * - 중간: 중립 신호
         */
        fun fromBollingerBand(positionPercent: Double): IndicatorSignalType {
            return when {
                positionPercent <= 30 -> BUY_SIGNAL
                positionPercent >= 70 -> SELL_SIGNAL
                else -> NEUTRAL
            }
        }

        /**
         * RSI 기준:
         * - 30 미만: 매수 신호
         * - 30~50: 약세 신호
         * - 50~70: 강세 신호
         * - 70 이상: 매도 신호
         */
        fun fromRsi(rsi: Double): IndicatorSignalType {
            return when {
                rsi < 30 -> BUY_SIGNAL
                rsi in 30.0..45.0 -> BEARISH
                rsi in 45.0..55.0 -> NEUTRAL
                rsi in 55.0..70.0 -> BULLISH
                rsi >= 70 -> SELL_SIGNAL
                else -> NEUTRAL
            }
        }
    }
}