package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.QStockSymbol
import com.hsryuuu.stock.domain.stock.model.entity.StockSymbol
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomStockSymbolRepository(
    private val queryFactory: JPAQueryFactory
) {

    private val stockSymbol = QStockSymbol.stockSymbol

    /**
     * symbol + timeframe 조건으로 조회, bucketStartUtc 오름차순
     */
    fun searchSymbol(query: String): List<StockSymbol> {
        val queryCond = stockSymbol.symbol.startsWith(query)
        return queryFactory
            .selectFrom(stockSymbol)
            .where(
                queryCond
            )
            .fetch()
    }
}