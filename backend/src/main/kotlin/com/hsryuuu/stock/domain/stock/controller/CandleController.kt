package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.CandleService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RequestMapping("/api/stock/candles")
@RestController
class CandleController(
    private val candleService: CandleService
) {

    @GetMapping("/{symbol}")
    fun getCandles(
        @PathVariable symbol: String,
        @RequestParam(name = "tf", defaultValue = "DAY1") timeframe: Timeframe,
        @RequestParam(name = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate?,
    ): List<CandleDto> {

        return candleService.getCandles(symbol, timeframe, from ?: LocalDate.now().minusYears(3L));
    }
}