package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.model.dto.StockPriceInfo
import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.model.dto.SymbolStatus
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.SymbolService
import org.springframework.web.bind.annotation.*


@RequestMapping("/api/stock/symbols")
@RestController
class SymbolController(
    private val symbolService: SymbolService,
) {

    @GetMapping("/{symbol}")
    fun getCandles(
        @PathVariable symbol: String,
    ): StockSymbolDto = symbolService.findBySymbol(symbol)


    @GetMapping("/search")
    fun searchCandles(
        @RequestParam(name = "q") query: String,
    ): List<StockSymbolDto> = symbolService.search(query)


    @GetMapping("/{symbol}/current-price")
    fun getCurrentPrice(
        @PathVariable symbol: String,
    ): StockPriceInfo = symbolService.getCurrentPrice(symbol)


    @GetMapping("/{symbol}/status")
    fun getCandleCollectStatus(
        @PathVariable symbol: String,
        @RequestParam(required = false, defaultValue = "DAY1") tf: Timeframe = Timeframe.DAY1
    ): SymbolStatus {
        return symbolService.getCollectStatus(symbol, tf);
    }

}