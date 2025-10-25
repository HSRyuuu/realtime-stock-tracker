package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.type.IndicatorSignalType
import com.hsryuuu.stock.domain.stock.event.CandleEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.IndicatorSignals
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IndicatorService(
    private val candleRepository: CustomStockCandleRepository,
    private val candleEventProducer: CandleEventProducer
) {

    @Transactional(readOnly = true)
    fun getBollingerBandSignal(symbol: String): IndicatorSignals.BollingerBand {

        val latestCandle = candleRepository.findLatestCandle(symbol, Timeframe.DAY1)
        if (latestCandle == null) {
            candleEventProducer.sendCandleCollectEvent(symbol, Timeframe.DAY1)
            return IndicatorSignals.BollingerBand(ready = false)
        }
        val bollingerBand = candleRepository.findLatestBollingerBand(symbol, Timeframe.DAY1)
        if (bollingerBand == null || bollingerBand.date.isBefore(latestCandle.date)) {
            candleEventProducer.sendBollingerBandCalculateEvent(symbol, Timeframe.DAY1)
            return IndicatorSignals.BollingerBand(ready = false)

        }
        val currentPrice = latestCandle.close.toDouble()
        val upper = bollingerBand.upper.toDouble()
        val lower = bollingerBand.lower.toDouble()

        val positionPercent = ((currentPrice - lower) / (upper - lower)) * 100
        val signalType = IndicatorSignalType.fromBollingerBand(positionPercent)

        return IndicatorSignals.BollingerBand(
            ready = true,
            signalType,
            currentPrice,
            upper,
            lower,
            bollingerBand.middle.toDouble()
        )
    }

    @Transactional(readOnly = true)
    fun getRsiSignal(symbol: String): IndicatorSignals.RSI {
        val latestCandle = candleRepository.findLatestCandle(symbol, Timeframe.DAY1)
        if (latestCandle == null) {
            candleEventProducer.sendCandleCollectEvent(symbol, Timeframe.DAY1)
            return IndicatorSignals.RSI(ready = false)
        }

        val rsi = candleRepository.findLatestRSI(symbol, Timeframe.DAY1)
        if (rsi == null || rsi.date.isBefore(latestCandle.date)) {
            candleEventProducer.sendRSICalculateEvent(symbol, Timeframe.DAY1)
            return IndicatorSignals.RSI(ready = false)
        }

        val signalType = IndicatorSignalType.fromRsi(rsi.rsi.toDouble())

        return IndicatorSignals.RSI(
            ready = true,
            signalType,
            rsi.rsi.toDouble(),
            rsi.avgGain.toDouble(),
            rsi.avgLoss.toDouble(),
            period = rsi.period
        )

    }


}