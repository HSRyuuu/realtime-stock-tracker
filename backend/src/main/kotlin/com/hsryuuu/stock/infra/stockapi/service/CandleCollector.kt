package com.hsryuuu.stock.infra.stockapi.service

import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.StockCandleRepository
import com.hsryuuu.stock.infra.stockapi.provider.TwelveDataStockDataProvider
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CandleCollector(
    private val stockDataProvider: TwelveDataStockDataProvider,
    private val stockCandleRepository: StockCandleRepository,
) {

    fun collectAndSave(
        symbol: String,
        timeframe: Timeframe,
        collectStartDate: LocalDate = LocalDate.now().minusYears(5L)
    ): List<CandleDto> {
        val collectResult = stockDataProvider.getTimeSeries(symbol, timeframe, collectStartDate)

        return if (collectResult.success && collectResult.data != null) {
            saveCandles(symbol, timeframe, collectResult.data.candles)
                .map { CandleDto.fromEntity(it) }
                .toList()
        } else {
            emptyList()
        }
    }

    private fun saveCandles(symbol: String, timeframe: Timeframe, candles: List<CandleDto>): List<StockCandle> =
        stockCandleRepository.saveAll(candles.map {
            CandleDto.toEntity(
                symbol,
                timeframe,
                it
            )
        })

}