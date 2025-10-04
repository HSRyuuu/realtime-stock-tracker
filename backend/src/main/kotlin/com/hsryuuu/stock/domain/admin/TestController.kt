package com.hsryuuu.stock.domain.admin

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.CandleService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RequestMapping("/api/test")
@RestController
class TestController(
    private val stockMasterService: StockMasterService,
    private val candleService: CandleService
) {

    @GetMapping("/all-stocks")
    fun collectAllStocks() {
        stockMasterService.upsertStockSymbols()
    }

    @GetMapping("/all-etfs")
    fun collectAllETFs() {
        stockMasterService.upsertETFSymbols()
    }

    @PostMapping("/collect-all-stocks")
    fun collectAllStocksPost(@RequestBody body: List<String>) {
        for (symbol in body) {
            candleService.getCandles(symbol, Timeframe.DAY1, LocalDate.now().minusDays(100L))
        }
    }
}