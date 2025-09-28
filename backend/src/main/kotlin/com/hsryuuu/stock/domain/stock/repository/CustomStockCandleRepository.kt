package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.QStockCandle
import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomStockCandleRepository(
    private val queryFactory: JPAQueryFactory
) {

    private val stockCandle = QStockCandle.stockCandle

    /**
     * symbol + timeframe 조건으로 조회, bucketStartUtc 오름차순
     */
    fun findBySymbolAndTimeframe(symbol: String, timeframe: Timeframe, epochMilli: Long): List<StockCandle> {
        return queryFactory
            .selectFrom(stockCandle)
            .where(
                stockCandle.symbol.eq(symbol),
                stockCandle.timeframe.eq(timeframe),
                stockCandle.bucketStartUtc.goe(epochMilli)
            )
            .orderBy(stockCandle.bucketStartUtc.desc())
            .fetch()
    }
}