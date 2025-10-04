package com.hsryuuu.stock.infra.stockapi.service

import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils.TIME_ZONE_AMERICA_NEW_YORK
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.infra.stockapi.provider.stock.TwelveDataStockDataProvider
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class CandleCollector(
    private val stockDataProvider: TwelveDataStockDataProvider,
    private val stockCandleRepository: CustomStockCandleRepository,
) {

    fun getCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        val referenceDate = resolveReferenceDate(TIME_ZONE_AMERICA_NEW_YORK) // 마지막 장 open 날짜 (zone 은 일단 하드코딩)
        val existingCandles = findCandles(symbol, timeframe, from)
        val latestCandle = existingCandles.lastOrNull()

        val collectResult = when {
            existingCandles.isEmpty() -> {
                // 처음 조회하는 경우
                collectAndSave(symbol, timeframe)
            }

            latestCandle?.datetime?.toLocalDate()?.isBefore(referenceDate) == true -> {
                // 기존에 존재하는 경우
                collectAndSave(symbol, timeframe, latestCandle.datetime.toLocalDate().plusDays(1L))
            }

            else -> return existingCandles
        }
        val totalCandles = existingCandles + collectResult

        if (from.isAfter(existingCandles.first().datetime.toLocalDate())) {
            return totalCandles.filter { it.datetime.toLocalDate() >= from }
        }

        return totalCandles
    }

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

    /**
     * 주말일 경우에 휴장이므로, 지난 금요일을 반환
     */
    private fun resolveReferenceDate(zoneId: String): LocalDate {
        val now = LocalDateTime.now().plusDays(1L).minusHours(1L)
        return if (StockTimeUtils.isWeekendCloseTime(now, zoneId)) {
            TimeUtils.getLastFriday(LocalDate.now())
        } else {
            TimeUtils.getYesterday(LocalDate.now())
        }
    }

    private fun findCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        val refEpochMillis = TimeUtils.getZoneEpochMilli(from.atStartOfDay(), TIME_ZONE_AMERICA_NEW_YORK)   // 조회 기준 일시
        return stockCandleRepository.findBySymbolAndTimeframeOrderByTimeAsc(symbol, timeframe, refEpochMillis)
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