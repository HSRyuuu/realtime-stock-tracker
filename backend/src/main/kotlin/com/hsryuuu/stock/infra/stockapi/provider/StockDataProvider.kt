package com.hsryuuu.stock.infra.stockapi.provider

import com.hsryuuu.stock.application.dto.ProcessResult
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import java.time.LocalDate

interface StockDataProvider {
    fun getTimeSeries(symbol: String, timeframe: Timeframe, startDate: LocalDate): ProcessResult<CandleResponse>
}