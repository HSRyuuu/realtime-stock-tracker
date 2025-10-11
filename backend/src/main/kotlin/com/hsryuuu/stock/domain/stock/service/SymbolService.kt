package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.stock.event.CandleCollectEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.StockPriceInfo
import com.hsryuuu.stock.domain.stock.model.dto.StockSymbolDto
import com.hsryuuu.stock.domain.stock.model.dto.SymbolStatus
import com.hsryuuu.stock.domain.stock.model.entity.StockSymbol
import com.hsryuuu.stock.domain.stock.model.type.CandleCollectState
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
    private val candleCollector: CandleCollector,
    private val candleStatusService: CandleStatusService,
    private val candleCollectEventProducer: CandleCollectEventProducer
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

    @Transactional(readOnly = true)
    fun getCollectStatus(symbol: String, timeframe: Timeframe): SymbolStatus {
        // redis 에 수집 상태 정보가 있는 경우
        val existingStatus = candleStatusService.get(symbol, Timeframe.DAY1)
        if (existingStatus != null && existingStatus.state in listOf(
                CandleCollectState.PENDING,
                CandleCollectState.RUNNING
            )
        ) {
            return existingStatus
        }
        // 수집 상태가 없는 경우 수집 프로세스 시작
        val findLatestCandle = candleRepository.findLatestCandle(symbol, Timeframe.DAY1)
        val refDate = StockTimeUtils.resolveReferenceDate(TimeUtils.TIME_ZONE_AMERICA_NEW_YORK)

        return when {
            findLatestCandle == null -> {
                createCollectCandleEvent(symbol)
                SymbolStatus(symbol, false, CandleCollectState.PENDING, "데이터 수집을 시작합니다.")
            }

            findLatestCandle.date.isBefore(refDate) -> {
                createCollectCandleEvent(symbol)
                SymbolStatus(
                    symbol,
                    false,
                    CandleCollectState.PENDING,
                    "최신 데이터 수집을 시작합니다."
                )
            }

            else -> {
                SymbolStatus(symbol, true, CandleCollectState.SUCCESS, "수집 완료 상태입니다.")
            }
        }
    }

    private fun createCollectCandleEvent(symbol: String, timeframe: Timeframe = Timeframe.DAY1) {
        candleStatusService.setPending(symbol, timeframe)
        candleCollectEventProducer.sendCandleCollectEvent(symbol, timeframe)
        candleStatusService.setRunning(symbol, timeframe)
        log.info("✅수집 이벤트 발행 완료: symbol=$symbol, timeframe=$timeframe")
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