package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.repository.CustomStockSymbolRepository
import com.hsryuuu.stock.domain.stock.repository.StockSymbolRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class SymbolService(
    private val stockSymbolRepository: StockSymbolRepository,
    private val customSymbolRepository: CustomStockSymbolRepository
) {
    @Transactional(readOnly = true)
    fun findBySymbol(symbol: String): StockSymbolDto {
        val stockSymbol = stockSymbolRepository.findBySymbol(symbol) ?: throw GlobalException(
            HttpStatus.NOT_FOUND,
            GlobalErrorMessage.resourceNotFound("symbol=$symbol")
        )
        StockSymbolDto.from(stockSymbol).let {
            return it
        }
    }

    @Transactional(readOnly = true)
    fun search(query: String): List<StockSymbolDto> {
        return customSymbolRepository.searchSymbol(query)
            .map { StockSymbolDto.from(it) }
    }
}