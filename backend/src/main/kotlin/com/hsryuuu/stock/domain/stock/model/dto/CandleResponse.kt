package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.domain.stock.model.type.Timeframe

data class CandleResponse(
    val meta: Meta,
    val candles: List<CandleDto>? = emptyList()
) {
    data class Meta(
        val symbol: String,
        val timeframe: Timeframe,
        val currency: String,
        val exchangeTimezone: String,
        val exchange: String,
        val micCode: String,
        val type: String,
    )
}