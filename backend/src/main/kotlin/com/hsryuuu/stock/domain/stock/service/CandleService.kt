package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.type.TimeZoneId
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.StockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.StockSymbolRepository
import com.hsryuuu.stock.infra.stock.provider.TwelveDataStockDataProvider
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class CandleService(
    private val stockSymbolRepository: StockSymbolRepository,
    private val stockCandleRepository: StockCandleRepository,
    private val customStockCandleRepository: CustomStockCandleRepository,
    private val stockDataProvider: TwelveDataStockDataProvider
) {

    fun getCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        if (!stockSymbolRepository.existsBySymbol(symbol)) {
            throw GlobalException(HttpStatus.NOT_FOUND, GlobalErrorMessage.resourceNotFound("심볼"))
        }
        val referenceDate = resolveReferenceDate()
        if (!stockCandleRepository.existsBySymbolAndDate(symbol, referenceDate)) {
            collectAndSaveCandles(symbol, timeframe)
        }
        // 일단 한국 기준
        val epochMilli = from.atStartOfDay().atZone(ZoneId.of(TimeZoneId.ASIA_SEOUL.value))
            .toInstant().toEpochMilli()

        customStockCandleRepository.findBySymbolAndTimeframe(symbol, timeframe, epochMilli)
            .map { CandleDto.fromEntity(it) }.toList().let {
                return it
            }
    }

    /**
     * 주말일 경우에 휴장이므로, 지난 금요일을 반환
     */
    private fun resolveReferenceDate(): LocalDate {
        val today = LocalDate.now()
        var referenceDate = TimeUtils.getYesterday(today)
        if (TimeUtils.isWeekend(today)) {
            referenceDate = TimeUtils.getLastFriday(today)
        }
        return referenceDate
    }

    private fun collectAndSaveCandles(symbol: String, timeframe: Timeframe) {
        val candleResponse = stockDataProvider.getTimeSeries(symbol, timeframe) ?: return
        val candles = candleResponse.candles
        stockCandleRepository.saveAll(candles.map { CandleDto.toEntity(symbol, timeframe, it) })

    }
}