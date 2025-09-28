package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.application.type.ProcessResult
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.CandleService
import com.hsryuuu.stock.infra.stock.provider.TwelveDataStockDataProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/api/stock")
@RestController
class TestCandleController(
    private val twelveDataStockDataProvider: TwelveDataStockDataProvider,
    private val candleService: CandleService
) {

    @GetMapping("/time-series")
    fun testTwelveData(
        @RequestParam(
            name = "symbol",
            defaultValue = "AAPL"
        ) symbol: String
    ): ProcessResult<CandleResponse> {
        return twelveDataStockDataProvider.getTimeSeries(symbol, Timeframe.DAY1);
    }

    @GetMapping("/time-series/test")
    fun testTimeSeries(@RequestParam(name = "symbol", defaultValue = "AAPL") symbol: String): List<CandleDto> {
        return candleService.getCandles(symbol);
    }
}