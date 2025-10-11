package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.QStockCandle
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomStockCandleRepository(
    private val queryFactory: JPAQueryFactory,
    private val stockCandleRepository: StockCandleRepository,
) {

    private val stockCandle = QStockCandle.stockCandle

    fun saveAll(candles: List<StockCandle>): List<StockCandle> =
        stockCandleRepository.saveAll(candles)

    /**
     * symbol + timeframe 조건으로 조회, bucketStartUtc 오름차순
     */
    fun findBySymbolAndTimeframeOrderByTimeAsc(
        symbol: String,
        timeframe: Timeframe,
        startEpochMillis: Long
    ): List<StockCandle> {
        return queryFactory
            .selectFrom(stockCandle)
            .where(
                stockCandle.symbol.eq(symbol),
                stockCandle.timeframe.eq(timeframe),
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
        return queryFactory.select(stockCandle)
            .from(stockCandle)
            .where(
                stockCandle.symbol.eq(symbol),
                stockCandle.timeframe.eq(timeframe)
            )
            .orderBy(stockCandle.bucketStartUtc.desc())
            .limit(1)
            .fetchOne()
    }
}