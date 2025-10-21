package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.event.CandleEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.IndicatorSignals
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.service.IndicatorService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "기술적 지표 API")
@RequestMapping("/api/indicators")
@RestController
class IndicatorController(
    private val indicatorService: IndicatorService,
    private val candleEventProducer: CandleEventProducer
) {

    @GetMapping("/bollinger-bands/{symbol}")
    fun getBollingerBand(@PathVariable symbol: String): IndicatorSignals.BollingerBand {
        return indicatorService.getBollingerBandSignal(symbol)
    }

    @GetMapping("/rsi/{symbol}")
    fun getRSI(@PathVariable symbol: String): IndicatorSignals.RSI {
        return indicatorService.getRsiSignal(symbol)
    }


    @PostMapping("/test-bb")
    fun test(
        @RequestParam symbol: String,
        @RequestParam(name = "tf", defaultValue = "DAY1") timeframe: Timeframe,
    ) {
        candleEventProducer.sendBollingerBandCalculateEvent(symbol, timeframe)
    }
}