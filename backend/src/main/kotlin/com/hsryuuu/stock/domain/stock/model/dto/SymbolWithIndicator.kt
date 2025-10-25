package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.util.IndicatorUtils
import java.math.BigDecimal
import java.time.LocalDate

data class SymbolWithIndicator(
    // Candle
    val symbol: String,
    val timeframe: Timeframe,
    val date: LocalDate,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    // Bollinger Band
    val upper: BigDecimal?,
    val middle: BigDecimal?,
    val lower: BigDecimal?,
    // RSI
    val rsi: BigDecimal?,
    val avgGain: BigDecimal?,
    val avgLoss: BigDecimal?,
    val period: Int?
)

data class SymbolWithIndicatorSignals(
    val symbol: String,
    val timeframe: Timeframe,
    val date: LocalDate,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val bollingerBand: IndicatorSignals.BollingerBand,
    val rsi: IndicatorSignals.RSI,
) {
    companion object {

        fun from(swi: SymbolWithIndicator): SymbolWithIndicatorSignals {
            val bb = IndicatorUtils.getBollingerBandCurrentPosition(
                swi.close.toDouble(),
                swi.upper?.toDouble() ?: 0.0,
                swi.lower?.toDouble() ?: 0.0,
                swi.middle?.toDouble() ?: 0.0
            )
            val rsi = IndicatorUtils.getRSICurrentPosition(
                swi.rsi?.toDouble() ?: 0.0,
                swi.avgGain?.toDouble() ?: 0.0,
                swi.avgLoss?.toDouble() ?: 0.0,
                swi.period ?: 0
            )
            return SymbolWithIndicatorSignals(
                swi.symbol,
                swi.timeframe,
                swi.date,
                swi.open,
                swi.high,
                swi.low,
                swi.close,
                bollingerBand = bb,
                rsi = rsi,
            )
        }
    }
}