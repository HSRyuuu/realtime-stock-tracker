package com.hsryuuu.stock.infra.stockapi.service

import com.hsryuuu.stock.application.dto.ProcessResult
import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils.TIME_ZONE_AMERICA_NEW_YORK
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.CandleResponse
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.infra.stockapi.provider.stock.TwelveDataStockDataProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CandleCollector(
    private val stockDataProvider: TwelveDataStockDataProvider,
    private val candleRepository: CustomStockCandleRepository,
) {

    private val log = LoggerFactory.getLogger(CandleCollector::class.java)


    @Transactional
    fun collectAndSaveCandles(symbol: String, timeframe: Timeframe) {
        try {
            val referenceDate =
                StockTimeUtils.resolveReferenceDate(TIME_ZONE_AMERICA_NEW_YORK) // 마지막 장 open 날짜 (zone 은 일단 하드코딩)
            // 일봉 / 분봉 상관없이 최신 데이터
            val latestCandle = candleRepository.findLatestCandle(symbol)
            when {
                latestCandle == null -> {
                    val result = collectCandles(symbol, timeframe)
                    saveCandles(symbol, timeframe, result)
                }

                latestCandle.date.isBefore(referenceDate) -> {
                    val result = collectCandles(symbol, timeframe, latestCandle.date.plusDays(1L))
                    saveCandles(symbol, timeframe, result)
                }

                else -> {
                    log.info("Candles already collected: symbol: {}", symbol)
                    return
                }
            }
        } catch (e: Exception) {
            log.error("❌ Candle data collection failed for symbol={}, error={}", symbol, e.message, e)
            throw e
        }

    }

    private fun collectCandles(
        symbol: String,
        timeframe: Timeframe,
        collectStartDate: LocalDate = LocalDate.now().minusYears(20L)
    ): ProcessResult<CandleResponse> = stockDataProvider.getTimeSeries(symbol, timeframe, collectStartDate)

    private fun saveCandles(symbol: String, timeframe: Timeframe, collectResult: ProcessResult<CandleResponse>) {
        if (!collectResult.success || collectResult.data == null) {
            log.info("❌Collected Candle is empty")
            return
        }
        candleRepository.saveAll(collectResult.data.candles.map {
            CandleDto.toEntity(
                symbol,
                timeframe,
                it
            )
        })
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        val referenceDate =
            StockTimeUtils.resolveReferenceDate(TIME_ZONE_AMERICA_NEW_YORK) // 마지막 장 open 날짜 (zone 은 일단 하드코딩)
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

        if (from.isAfter(totalCandles.first().datetime.toLocalDate())) {
            return totalCandles.filter { it.datetime.toLocalDate() >= from }
        }

        return totalCandles
    }

    private fun collectAndSave(
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


    private fun findCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        val refEpochMillis = TimeUtils.getZoneEpochMilli(from.atStartOfDay(), TIME_ZONE_AMERICA_NEW_YORK)   // 조회 기준 일시
        return candleRepository.findBySymbolAndTimeframeOrderByTimeAsc(symbol, timeframe, refEpochMillis)
            .map { CandleDto.fromEntity(it) }.toList()
    }

    private fun saveCandles(symbol: String, timeframe: Timeframe, candles: List<CandleDto>): List<StockCandle> =
        candleRepository.saveAll(candles.map {
            CandleDto.toEntity(
                symbol,
                timeframe,
                it
            )
        })

}