package com.hsryuuu.stock.domain.stock.model.dto

class IndicatorValues {

    data class BollingerBand(
        val upper: Double, // 상단 밴드
        val middle: Double, // 중단 밴드
        val lower: Double, // 하단 밴드
        val standardDeviation: Double, // 표준 편차
        val period: Int,
        val kValue: Double
    )

    data class RSI(
        val rsi: Double,
        val avgGain: Double,
        val avgLoss: Double,
        val period: Int
    )
}