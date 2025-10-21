package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.event.CandleEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.BollingerBandSignal
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.IndicatorService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "기술적 지표 API")
@RequestMapping("/api/stock/indicators")
@RestController
class IndicatorController(
    private val indicatorService: IndicatorService,
    private val candleEventProducer: CandleEventProducer
) {

    @GetMapping("/bollinger-bands/{symbol}")
    fun getBollingerBand(@PathVariable symbol: String): BollingerBandSignal {
        return indicatorService.getBollingerBandSignal(symbol)
    }


    @PostMapping("/test-bb")
    fun test(
        @RequestParam symbol: String,
        @RequestParam(name = "tf", defaultValue = "DAY1") timeframe: Timeframe,
    ) {
        candleEventProducer.sendBollingerBandCalculateEvent(symbol, timeframe)
    }
}