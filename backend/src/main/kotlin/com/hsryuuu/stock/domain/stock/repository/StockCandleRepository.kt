package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.entity.StockCandleId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface StockCandleRepository : JpaRepository<StockCandle, StockCandleId> {
    fun existsBySymbolAndDate(symbol: String, date: LocalDate): Boolean
    fun findFirstBySymbolOrderByBucketStartUtcDesc(symbol: String): StockCandle?
}
