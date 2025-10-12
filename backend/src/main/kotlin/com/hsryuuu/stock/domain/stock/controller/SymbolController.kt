package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.model.dto.StockPriceInfo
import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.service.SymbolService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "종목(symbol) API")
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
}