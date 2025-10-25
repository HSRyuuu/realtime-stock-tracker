package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.*
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CustomStockCandleRepository(
    private val queryFactory: JPAQueryFactory,
    private val stockCandleRepository: StockCandleRepository,
    private val bollingerBandRepository: BollingerBandRepository,
    private val stockRsiRepository: StockRsiRepository,
) {

    private val stockCandle = QStockCandle.stockCandle
    private val bollingerBand = QBollingerBand.bollingerBand
    private val rsi = QStockRsi.stockRsi

    fun saveAllCandles(candles: List<StockCandle>): List<StockCandle> =
        stockCandleRepository.saveAll(candles)


    fun saveAllBollingerBands(bollingerBands: List<BollingerBand>): List<BollingerBand> =
        bollingerBandRepository.saveAll(bollingerBands)

    fun saveAllRsi(rsiList: List<StockRsi>): List<StockRsi> =
        stockRsiRepository.saveAll(rsiList)

    /**
     * symbol + timeframe 조건으로 조회, bucketStartUtc 오름차순
     */
    fun findBySymbolAndTimeframeOrderByTimeAsc(
        symbol: String,
        timeframe: Timeframe,
        startEpochMillis: Long
    ): List<StockCandle> {
        val findCond = getCandleSymbolAndTimeframeCondition(symbol, timeframe)
        return queryFactory
            .selectFrom(stockCandle)
            .where(
                findCond,
                stockCandle.bucketStartUtc.goe(startEpochMillis)
            )
            .orderBy(stockCandle.bucketStartUtc.asc())
            .fetch()
    }

    fun findLatestCandle(symbol: String): StockCandle? {
        return queryFactory.select(stockCandle)
            .from(stockCandle)
            .where(stockCandle.symbol.eq(symbol))
            .orderBy(stockCandle.bucketStartUtc.desc())
            .limit(1)
            .fetchOne()
    }

    fun findLatestCandle(symbol: String, timeframe: Timeframe): StockCandle? {
        val findCond = getCandleSymbolAndTimeframeCondition(symbol, timeframe)

        return queryFactory.selectFrom(stockCandle)
            .where(findCond)
            .orderBy(stockCandle.bucketStartUtc.desc())
            .limit(1)
            .fetchFirst()
    }

    fun findLatestBollingerBand(symbol: String, timeframe: Timeframe): BollingerBand? {
        val findCond = getBollingerBandSymbolAndTimeframeCondition(symbol, timeframe)
        return queryFactory.selectFrom(bollingerBand)
            .where(findCond)
            .orderBy(bollingerBand.date.desc())
            .fetchFirst()
    }

    fun findLatestRSI(symbol: String, timeframe: Timeframe): StockRsi? {
        val findCond = getRSISymbolAndTimeframeCondition(symbol, timeframe)
        return queryFactory.selectFrom(rsi)
            .where(findCond)
            .orderBy(rsi.date.desc())
            .fetchFirst()
    }

    fun findCandlesToCalcIndicators(
        symbol: String,
        timeframe: Timeframe,
        date: LocalDate,
        period: Int
    ): List<StockCandle> {
        val findCond = getCandleSymbolAndTimeframeCondition(symbol, timeframe)

        val stockCandlesPeriod = queryFactory.selectFrom(stockCandle)
            .where(
                findCond,
                stockCandle.date.before(date)
            ).orderBy(stockCandle.date.desc())
            .limit(period.toLong())
            .fetch()
            .reversed()

        val stockCandlesAfter = queryFactory.selectFrom(stockCandle)
            .where(
                findCond,
                stockCandle.date.goe(date)
            ).orderBy(stockCandle.date.asc())
            .fetch()

        return stockCandlesPeriod + stockCandlesAfter
    }

    fun findLatestBollingerBandDate(symbol: String, timeframe: Timeframe): LocalDate? {
        val findCond = getBollingerBandSymbolAndTimeframeCondition(symbol, timeframe)
        return queryFactory.select(bollingerBand.date)
            .from(bollingerBand)
            .where(findCond)
            .orderBy(bollingerBand.date.desc())
            .fetchFirst()
    }

    fun findLatestRSIDate(symbol: String, timeframe: Timeframe): LocalDate? {
        val findCond = getRSISymbolAndTimeframeCondition(symbol, timeframe)
        return queryFactory.select(rsi.date)
            .from(rsi)
            .where(findCond)
            .orderBy(rsi.date.desc())
            .fetchFirst()
    }


    private fun getCandleSymbolAndTimeframeCondition(symbol: String, timeframe: Timeframe): BooleanExpression {
        return stockCandle.symbol.eq(symbol)
            .and(stockCandle.timeframe.eq(timeframe))
    }

    private fun getBollingerBandSymbolAndTimeframeCondition(symbol: String, timeframe: Timeframe): BooleanExpression {
        return bollingerBand.symbol.eq(symbol)
            .and(bollingerBand.timeframe.eq(timeframe))
    }

    private fun getRSISymbolAndTimeframeCondition(symbol: String, timeframe: Timeframe): BooleanExpression {
        return rsi.symbol.eq(symbol)
            .and(rsi.timeframe.eq(timeframe))
    }
}