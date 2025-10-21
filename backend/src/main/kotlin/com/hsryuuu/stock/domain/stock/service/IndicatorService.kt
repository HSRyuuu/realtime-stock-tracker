package com.hsryuuu.stock.domain.stock.service

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.hsryuuu.stock.domain.stock.repository.CustomStockCandleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class IndicatorService(
    private val candleRepository: CustomStockCandleRepository,
) {

    @Transactional
    fun getBollingerBands(symbol: String, timeframe: Timeframe, start: LocalDate) {
        val findAllBollingerBands = candleRepository.findAllBollingerBands(symbol, timeframe, start)

    }


}