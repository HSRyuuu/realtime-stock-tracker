package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.StockSymbol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockSymbolRepository : JpaRepository<StockSymbol, Long> {

    fun existsBySymbol(symbol: String): Boolean
    fun findBySymbol(symbol: String): StockSymbol?
}