package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.StockCandleRepository
import com.hsryuuu.stock.infra.stock.provider.TwelveDataStockDataProvider
import org.springframework.stereotype.Service

@Service
class CandleService(
    private val stockCandleRepository: StockCandleRepository,
    private val stockDataProvider: TwelveDataStockDataProvider
) {

    fun getCandles(symbol: String, timeframe: Timeframe): List<CandleDto> {

        val stockCandles = stockCandleRepository.findAllBySymbol(symbol)
        return stockCandles.map { CandleDto.fromEntity(it) }.toList();
    }
}