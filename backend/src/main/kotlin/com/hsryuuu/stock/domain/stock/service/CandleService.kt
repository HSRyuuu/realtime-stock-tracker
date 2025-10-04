package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockSymbolRepository
import com.hsryuuu.stock.infra.stockapi.service.CandleCollector
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CandleService(
    private val stockSymbolRepository: CustomStockSymbolRepository,
    private val candleCollector: CandleCollector,
) {

    fun getCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        validateSymbolExists(symbol)
        return candleCollector.getCandles(symbol, timeframe, from)
    }

    private fun validateSymbolExists(symbol: String) {
        if (!stockSymbolRepository.existsBySymbol(symbol)) {
            throw GlobalException(HttpStatus.NOT_FOUND, GlobalErrorMessage.resourceNotFound("심볼"))
        }
    }


}