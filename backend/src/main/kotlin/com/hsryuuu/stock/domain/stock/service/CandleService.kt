package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.utils.StockTimeUtils
import com.hsryuuu.stock.application.utils.TimeUtils
import com.hsryuuu.stock.domain.stock.event.CandleCollectEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.dto.SymbolStatus
import com.hsryuuu.stock.domain.stock.model.type.CandleCollectState
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.repository.CustomStockSymbolRepository
import com.hsryuuu.stock.infra.stockapi.service.CandleCollector
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CandleService(
    private val stockSymbolRepository: CustomStockSymbolRepository,
    private val candleRepository: CustomStockCandleRepository,
    private val candleCollector: CandleCollector,
    private val candleStatusService: CandleStatusService,
    private val candleCollectEventProducer: CandleCollectEventProducer
) {

    private val log = LoggerFactory.getLogger(CandleService::class.java)

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

    @Transactional
    fun getCandles(symbol: String, timeframe: Timeframe, from: LocalDate): List<CandleDto> {
        validateSymbolExists(symbol)
        return candleCollector.getCandles(symbol, timeframe, from)
    }

    private fun validateSymbolExists(symbol: String) {
        if (!stockSymbolRepository.existsBySymbol(symbol)) {
            throw GlobalException(HttpStatus.NOT_FOUND, GlobalErrorMessage.resourceNotFound("심볼"))
        }
    }


}