package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.event.CandleEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.IndicatorSignals
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import com.hsryuuu.stock.domain.stock.util.IndicatorUtils
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
        return IndicatorUtils.getBollingerBandCurrentPosition(
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
        
        return IndicatorUtils.getRSICurrentPosition(
            rsi.rsi.toDouble(),
            rsi.avgGain.toDouble(),
            rsi.avgLoss.toDouble(),
            rsi.period
        )

    }


}