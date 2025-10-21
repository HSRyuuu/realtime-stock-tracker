package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.application.type.IndicatorSignalType
import com.hsryuuu.stock.domain.stock.event.CandleEventProducer
import com.hsryuuu.stock.domain.stock.model.dto.BollingerBandSignal
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
    fun getBollingerBandSignal(symbol: String): BollingerBandSignal {

        val latestCandle = candleRepository.findLatestCandle(symbol, Timeframe.DAY1)
        if (latestCandle == null) {
            candleEventProducer.sendCandleCollectEvent(symbol, Timeframe.DAY1)
            return BollingerBandSignal(ready = false)
        }
        val bollingerBand = candleRepository.findLatestBollingerBand(symbol, Timeframe.DAY1)
        if (bollingerBand == null) {
            candleEventProducer.sendBollingerBandCalculateEvent(symbol, Timeframe.DAY1)
            return BollingerBandSignal(ready = false)

        }
        val currentPrice = latestCandle.close.toDouble()
        val upper = bollingerBand.upper.toDouble()
        val lower = bollingerBand.lower.toDouble()

        val positionPercent = ((currentPrice - lower) / (upper - lower)) * 100
        val signalType = IndicatorSignalType.fromBollingerBand(positionPercent)

        return BollingerBandSignal(
            ready = true,
            signalType,
            currentPrice,
            upper,
            lower,
            bollingerBand.middle.toDouble()
        )
    }


}