package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.StockCandleId
import com.hsryuuu.stock.domain.stock.model.entity.StockRsi
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRsiRepository : JpaRepository<StockRsi, StockCandleId> {

}