package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.event.CandleEventProducer
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.IndicatorService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "기술적 지표 API")
@RequestMapping("/api/stock/indicators")
@RestController
class IndicatorController(
    private val indicatorService: IndicatorService,
    private val candleEventProducer: CandleEventProducer
) {

    @GetMapping("/bollinger-bands")
    fun getBollingerBands(
        @RequestParam symbol: String,
        @RequestParam(name = "tf", defaultValue = "DAY1") timeframe: Timeframe,
        @RequestParam(name = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") from: LocalDate?,
    ) {
        indicatorService.getBollingerBands(symbol, timeframe, from ?: LocalDate.now().minusYears(20L))
    }

    @PostMapping("/test-bb")
    fun test(
        @RequestParam symbol: String,
        @RequestParam(name = "tf", defaultValue = "DAY1") timeframe: Timeframe,
    ) {
        candleEventProducer.sendBollingerBandCalculateEvent(symbol, timeframe)
    }
}