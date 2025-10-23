package com.hsryuuu.stock.domain.stock.controller

import com.hsryuuu.stock.domain.stock.model.dto.IndicatorSignals
import com.hsryuuu.stock.domain.stock.service.IndicatorService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "기술적 지표 API")
@RequestMapping("/api/public/indicators")
@RestController
class IndicatorController(
    private val indicatorService: IndicatorService
) {

    @GetMapping("/bollinger-bands/{symbol}")
    fun getBollingerBand(@PathVariable symbol: String): IndicatorSignals.BollingerBand {
        return indicatorService.getBollingerBandSignal(symbol)
    }

    @GetMapping("/rsi/{symbol}")
    fun getRSI(@PathVariable symbol: String): IndicatorSignals.RSI {
        return indicatorService.getRsiSignal(symbol)
    }
}