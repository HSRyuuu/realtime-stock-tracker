package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.dto.SymbolWithIndicator
import com.hsryuuu.stock.domain.stock.model.entity.QBollingerBand
import com.hsryuuu.stock.domain.stock.model.entity.QStockCandle
import com.hsryuuu.stock.domain.stock.model.entity.QStockRsi
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomIndicatorRepository(
    private val queryFactory: JPAQueryFactory,
) {

    private val stockCandle = QStockCandle.stockCandle
    private val bollingerBand = QBollingerBand.bollingerBand
    private val rsi = QStockRsi.stockRsi


    fun getCurrentPriceWithIndicators(symbols: List<String>): List<SymbolWithIndicator> {
        val symbolCond = stockCandle.symbol.`in`(symbols)

        val sub = QStockCandle("sub")
        val latestDateSubQuery = JPAExpressions.select(sub.date.max())
            .from(sub)
            .where(sub.symbol.eq(stockCandle.symbol), sub.timeframe.eq(stockCandle.timeframe))

        return queryFactory.select(
            Projections.constructor(
                SymbolWithIndicator::class.java,
                stockCandle.symbol,
                stockCandle.timeframe,
                stockCandle.date,
                stockCandle.open,
                stockCandle.high,
                stockCandle.low,
                stockCandle.close,
                bollingerBand.upper,
                bollingerBand.middle,
                bollingerBand.lower,
                rsi.rsi,
                rsi.avgGain,
                rsi.avgLoss,
                rsi.period
            )
        )
            .from(stockCandle)
            .leftJoin(bollingerBand)
            .on(
                stockCandle.symbol.eq(bollingerBand.symbol),
                stockCandle.date.eq(bollingerBand.date),
                stockCandle.timeframe.eq(bollingerBand.timeframe)
            )
            .leftJoin(rsi)
            .on(
                stockCandle.symbol.eq(rsi.symbol),
                stockCandle.date.eq(rsi.date),
                stockCandle.timeframe.eq(rsi.timeframe)
            )
            .where(symbolCond, stockCandle.date.eq(latestDateSubQuery))
            .fetch()

    }
}