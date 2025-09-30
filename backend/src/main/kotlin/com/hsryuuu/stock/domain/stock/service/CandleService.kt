package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.StockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.StockSymbolRepository
import com.hsryuuu.stock.infra.stockapi.provider.TwelveDataStockDataProvider
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class CandleService(
    private val stockSymbolRepository: StockSymbolRepository,
    private val stockCandleRepository: StockCandleRepository,
    private val customStockCandleRepository: CustomStockCandleRepository,
    private val stockDataProvider: TwelveDataStockDataProvider,
) {

    fun getCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        if (!stockSymbolRepository.existsBySymbol(symbol)) {
            throw GlobalException(HttpStatus.NOT_FOUND, GlobalErrorMessage.resourceNotFound("심볼"))
        }
        val referenceDate = resolveReferenceDate(TimeUtils.TIME_ZONE_AMERICA_NEW_YORK) // 일단 하드코딩
        if (!stockCandleRepository.existsBySymbolAndDate(symbol, referenceDate)) {
            collectAndSaveCandles(symbol, timeframe)
        }
        // 미국 장 시작 시간
        val epochMilli = TimeUtils.getZoneEpochMilli(from.atStartOfDay())

        customStockCandleRepository.findBySymbolAndTimeframe(symbol, timeframe, epochMilli)
            .map { CandleDto.fromEntity(it) }.toList().let {
                return it
            }
    }

    /**
     * 주말일 경우에 휴장이므로, 지난 금요일을 반환
     */
    private fun resolveReferenceDate(zoneId: String): LocalDate {
        val isMarketOpen =
            StockTimeUtils.isMarketOpenNow(LocalDateTime.now(), zoneId)
        return if (!isMarketOpen) {
            TimeUtils.getLastFriday(LocalDate.now())
        } else {
            TimeUtils.getYesterday(LocalDate.now())
        }
    }

    private fun collectAndSaveCandles(symbol: String, timeframe: Timeframe) {
        val existingLatestCandle = stockCandleRepository.findFirstBySymbolOrderByBucketStartUtcDesc(symbol)

        var startDate = LocalDate.now().minusYears(5L)
        if (existingLatestCandle != null) {
            startDate = existingLatestCandle.date.plusDays(1L)
        }
        val candleResponse =
            stockDataProvider.getTimeSeries(symbol, timeframe, startDate) ?: return
        val candles = candleResponse.candles

        stockCandleRepository.saveAll(candles.map {
            CandleDto.toEntity(
                symbol,
                timeframe,
                it
            )
        })

    }
}