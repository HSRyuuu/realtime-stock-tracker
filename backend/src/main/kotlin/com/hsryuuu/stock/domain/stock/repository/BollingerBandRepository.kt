package com.hsryuuu.stock.domain.stock.repository

import com.hsryuuu.stock.domain.stock.model.entity.BollingerBand
import com.hsryuuu.stock.domain.stock.model.entity.StockCandleId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BollingerBandRepository : JpaRepository<BollingerBand, StockCandleId> {
}