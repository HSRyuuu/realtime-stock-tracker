package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.service.StockMasterService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/api/test")
@RestController
class TestController(
    private val stockMasterService: StockMasterService,
) {

    @GetMapping("/all-stocks")
    fun collectAllStocks() {
        stockMasterService.upsertStockSymbols()
    }

    @GetMapping("/all-etfs")
    fun collectAllETFs() {
        stockMasterService.upsertETFSymbols()
    }
}