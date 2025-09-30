package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.service.SymbolService
import org.springframework.web.bind.annotation.*


@RequestMapping("/api/stock/symbols")
@RestController
class SymbolController(
    private val symbolService: SymbolService,
    service: SymbolService
) {

    @GetMapping("/{symbol}")
    fun getCandles(
        @PathVariable symbol: String,
    ): StockSymbolDto {
        return symbolService.findBySymbol(symbol)
    }

    @GetMapping("/search")
    fun searchCandles(
        @RequestParam(name = "q") query: String,
    ): List<StockSymbolDto> {
        return symbolService.search(query)
    }

}