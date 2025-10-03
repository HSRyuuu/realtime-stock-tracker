package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.dto.ProcessResult
import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils.TIME_ZONE_AMERICA_NEW_YORK
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
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
        validateSymbolExists(symbol)
        val referenceDate = resolveReferenceDate(TIME_ZONE_AMERICA_NEW_YORK) // 마지막 장 open 날짜 (zone 은 일단 하드코딩)
        val existingCandles = findCandles(symbol, timeframe, from)
        val latestCandle = existingCandles.lastOrNull()

        val collectResult = when {
            existingCandles.isEmpty() -> {
                // 처음 조회하는 경우
                collectTimeSeriesData(symbol, timeframe)
            }

            latestCandle?.datetime?.toLocalDate()?.isBefore(referenceDate) == true -> {
                // 기존에 존재하는 경우
                collectTimeSeriesData(symbol, timeframe, latestCandle.datetime.toLocalDate().plusDays(1L))
            }

            else -> return existingCandles
        }

        if (collectResult.success && collectResult.data != null) {
            val savedData = saveCandles(symbol, timeframe, collectResult.data.candles)
                .map { CandleDto.fromEntity(it) }
                .toList()
            return existingCandles + savedData
        }

        return existingCandles
    }

    private fun validateSymbolExists(symbol: String) {
        if (!stockSymbolRepository.existsBySymbol(symbol)) {
            throw GlobalException(HttpStatus.NOT_FOUND, GlobalErrorMessage.resourceNotFound("심볼"))
        }
    }

    /**
     * 주말일 경우에 휴장이므로, 지난 금요일을 반환
     */
    private fun resolveReferenceDate(zoneId: String): LocalDate {
        val now2 = LocalDateTime.now()
        val now = LocalDateTime.now().plusDays(1L).minusHours(1L)
        return if (StockTimeUtils.isWeekendCloseTime(now, zoneId)) {
            TimeUtils.getLastFriday(LocalDate.now())
        } else {
            TimeUtils.getYesterday(LocalDate.now())
        }
    }

    private fun collectTimeSeriesData(
        symbol: String,
        timeframe: Timeframe,
        startDate: LocalDate = LocalDate.now().minusYears(5L)
    ):
            ProcessResult<CandleResponse> {
        return stockDataProvider.getTimeSeries(symbol, timeframe, startDate)
    }

    private fun findCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        val refEpochMillis = TimeUtils.getZoneEpochMilli(from.atStartOfDay(), TIME_ZONE_AMERICA_NEW_YORK)   // 조회 기준 일시
        return customStockCandleRepository.findBySymbolAndTimeframe(symbol, timeframe, refEpochMillis)
            .map { CandleDto.fromEntity(it) }.toList()
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