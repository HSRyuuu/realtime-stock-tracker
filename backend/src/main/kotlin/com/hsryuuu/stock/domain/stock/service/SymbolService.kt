package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils.TIME_ZONE_AMERICA_NEW_YORK
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.StockPriceInfo
import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.model.entity.StockSymbol
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.CustomStockSymbolRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class SymbolService(
    private val symbolRepository: CustomStockSymbolRepository,
    private val candleRepository: CustomStockCandleRepository,
) {

    private val log = LoggerFactory.getLogger(SymbolService::class.java)

    @Transactional(readOnly = true)
    fun findBySymbol(symbol: String): StockSymbolDto {
        val stockSymbol = findSymbolOrElseThrow(symbol)
        StockSymbolDto.from(stockSymbol).let {
            return it
        }
    }

    @Transactional(readOnly = true)
    fun search(query: String): List<StockSymbolDto> {
        return symbolRepository.searchSymbol(query)
            .map { StockSymbolDto.from(it) }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    fun getCurrentPrice(symbol: String): StockPriceInfo {
        val stockSymbol = findSymbolOrElseThrow(symbol)
        val latestCandle = findLatestCandleOrElseThrow(symbol) // 일봉/분봉 상관없이 최신 캔들
        val candles = get52WeekDayCandles(symbol) // 52주 일봉

        var previousCandle = candles.last()
        if (latestCandle.date == previousCandle.date && candles.size > 1) {
            previousCandle = candles[candles.size - 2]
        }

        val currentPrice = latestCandle.close.toDouble() // 현재가
        val previousClose = previousCandle.close.toDouble() // 이전 종가

        return StockPriceInfo(
            symbol = symbol,
            currentPrice = currentPrice,
            previousClose = previousClose,
            change = currentPrice - previousClose,
            changePercent = (currentPrice - previousClose) / previousClose * 100,
            volume = previousCandle.volume ?: 0,
            marketCap = 0,
            high52Week = candles.maxOf { it.high.toDouble() },
            low52Week = candles.minOf { it.low.toDouble() },
            lastUpdated = latestCandle.updatedAt ?: LocalDateTime.now(),
            currency = stockSymbol.currency ?: ""
        )
    }

    private fun get52WeekDayCandles(symbol: String): List<CandleDto> {
        val refDate = LocalDate.now().minusWeeks(52L)
        val refEpochMillis =
            TimeUtils.getZoneEpochMilli(refDate.atStartOfDay(), TIME_ZONE_AMERICA_NEW_YORK)   // 조회 기준 일시
        val candles = candleRepository.findBySymbolAndTimeframeOrderByTimeAsc(symbol, Timeframe.DAY1, refEpochMillis)
            .map { CandleDto.fromEntity(it) }
            .toList()

        if (candles.isEmpty()) {
            log.error("Candles not found: symbol={}, timeframe={}", symbol, Timeframe.DAY1)
            throw GlobalException(HttpStatus.NOT_FOUND, "캔들 데이터가 존재하지 않습니다.")
        }

        return candles
    }

    private fun findSymbolOrElseThrow(symbol: String): StockSymbol =
        symbolRepository.findBySymbol(symbol) ?: throw GlobalException(
            HttpStatus.NOT_FOUND,
            GlobalErrorMessage.resourceNotFound("symbol=$symbol")
        )

    private fun findLatestCandleOrElseThrow(symbol: String): CandleDto =
        candleRepository.findLatestCandle(symbol)?.let { CandleDto.fromEntity(it) }
            ?: throw GlobalException(HttpStatus.NOT_FOUND, "캔들이 존재하지 않습니다. symbol=$symbol")
}