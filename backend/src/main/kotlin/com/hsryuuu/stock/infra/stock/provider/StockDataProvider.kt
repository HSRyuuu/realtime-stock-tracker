package com.hsryuuu.stock.infra.stock.provider

import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.type.Timeframe

interface StockDataProvider {
    fun getTimeSeries(symbol: String, timeframe: Timeframe): CandleResponse?
}