package com.hsryuuu.stock.domain.market.controller

import com.hsryuuu.stock.domain.market.model.ExchangeRateDto
import com.hsryuuu.stock.domain.market.service.ExchangeService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "환율 API")
@RequestMapping("/api/exchange")
@RestController
class ExchangeController(
    private val exchangeService: ExchangeService,
) {

    @GetMapping("/current")
    fun getCurrentExchange(
        @RequestParam(name = "base", defaultValue = "USD") base: String,
        @RequestParam(name = "quote", defaultValue = "KRW") quote: String
    ): ExchangeRateDto = exchangeService.getCurrentExchangeRate(base, quote)
}