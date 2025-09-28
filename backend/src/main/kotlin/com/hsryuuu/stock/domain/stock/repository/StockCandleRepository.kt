package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.StockCandle
import com.hsryuuu.stock.domain.stock.model.entity.StockCandleId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockCandleRepository : JpaRepository<StockCandle, StockCandleId> {

    fun findAllBySymbol(symbol: String): List<StockCandle>
}