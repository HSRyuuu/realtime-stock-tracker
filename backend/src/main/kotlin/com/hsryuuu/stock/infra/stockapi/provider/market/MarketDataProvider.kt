package com.hsryuuu.stock.infra.stockapi.provider.market

import com.hsryuuu.stock.application.dto.ProcessResult
import com.hsryuuu.stock.infra.stockapi.response.TwelveData

interface MarketDataProvider {
    fun getExchangeRate(base: String, quote: String): ProcessResult<TwelveData.ExchangeRateResponse>
}