package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.StockPriceInfo
import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.model.entity.StockSymbol
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.CustomStockSymbolRepository
import com.hsryuuu.stock.infra.stockapi.service.CandleCollector
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
    private val candleCollector: CandleCollector
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
        val refDate = LocalDate.now().minusWeeks(52L)
        val stockSymbol = findSymbolOrElseThrow(symbol)

        // 일봉 / 분봉 상관없이 최신 데이터
        var latestCandle = candleRepository.findLatestCandle(symbol)?.let { CandleDto.fromEntity(it) }

        val candles = candleCollector.getCandles(symbol, Timeframe.DAY1, refDate)
            .filter { it.datetime.toLocalDate() >= refDate }.toList()

        if (candles.isEmpty()) throwUnknownSymbolException(symbol)

        // 마지막 캔들, 이전 캔들 값 세팅
        if (latestCandle == null || latestCandle.time < candles.last().time) {
            latestCandle = candles.last()
        }

        var previousCandle = candles.last()
        if (latestCandle.time == previousCandle.time && candles.size > 1) {
            previousCandle = candles[candles.size - 2]
        }

        val currentPrice = latestCandle.close.toDouble()
        val previousClose = previousCandle.close.toDouble()

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

    private fun findSymbolOrElseThrow(symbol: String): StockSymbol =
        symbolRepository.findBySymbol(symbol) ?: throw GlobalException(
            HttpStatus.NOT_FOUND,
            GlobalErrorMessage.resourceNotFound("symbol=$symbol")
        )

    private fun throwUnknownSymbolException(symbol: String): Nothing =
        throw GlobalException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "$symbol 데이터를 불러오는데 실패했습니다."
        )


}