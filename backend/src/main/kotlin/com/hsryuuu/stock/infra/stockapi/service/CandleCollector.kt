package com.hsryuuu.stock.infra.stockapi.service

import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils.TIME_ZONE_AMERICA_NEW_YORK
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.infra.stockapi.provider.stock.TwelveDataStockDataProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
                StockTimeUtils.resolveLastMarketOpenDate(TIME_ZONE_AMERICA_NEW_YORK) // 마지막 장 open 날짜 (zone 은 일단 하드코딩)
            // 일봉 / 분봉 상관없이 최신 데이터
            val latestCandle = candleRepository.findLatestCandle(symbol)
            val collectResult = when {
                latestCandle == null -> {
                    stockDataProvider.getTimeSeries(symbol, timeframe, LocalDate.now().minusYears(20L))
                }

                !latestCandle.date.isAfter(referenceDate) -> {
                    stockDataProvider.getTimeSeries(symbol, timeframe, latestCandle.date.minusDays(1L))
                }

                else -> {
                    log.info("Candles already collected: symbol: {}", symbol)
                    return
                }
            }

            if (!collectResult.success || collectResult.data == null) {
                log.info("❌Collected Candle is empty")
                return
            }


            saveCandles(symbol, timeframe, collectResult.data.candles)


        } catch (e: Exception) {
            log.error("❌ Candle data collection failed for symbol={}, error={}", symbol, e.message, e)
            throw e
        }

    }


    private fun saveCandles(symbol: String, timeframe: Timeframe, candles: List<CandleDto>) {
        val filtered = candles.filterNot { TimeUtils.isTodayInUs(it.date) }
        candleRepository.saveAll(filtered.map {
            CandleDto.toEntity(
                symbol,
                timeframe,
                it
            )
        })
    }

}