package com.hsryuuu.stock.infra.stock.api

import com.hsryuuu.stock.infra.stock.response.TwelveData
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "twelveDataClient",
    url = "https://api.twelvedata.com",
)
interface TwelveDataFeignClient {

    @GetMapping("/time_series")
    fun getTimeSeries(
        @RequestParam symbol: String = "AAPL",
        @RequestParam interval: String = "1day",
        @RequestParam apikey: String = "demo",
        @RequestParam format: String = "JSON",
    ): String

    @GetMapping("/stocks")
    fun getStocks(
        @RequestParam apikey: String = "demo",
    ): TwelveData.StockSymbolResult

    @GetMapping("/etfs")
    fun getETFs(
        @RequestParam apikey: String = "demo",
    ): TwelveData.StockSymbolResult
}
