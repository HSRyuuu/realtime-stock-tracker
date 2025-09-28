package com.hsryuuu.stock.domain.stock.model.entity

import com.hsryuuu.stock.application.type.CurrencyType
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "STOCK_CANDLE")
@IdClass(StockCandleId::class)
data class StockCandle(

    @Id
    @Column(nullable = false, length = 20)
    val symbol: String,

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val timeframe: Timeframe,

    @Id
    @Column(nullable = false)
    val bucketStartUtc: Long,

    @Column(nullable = false, precision = 18, scale = 8)
    val open: BigDecimal,

    @Column(nullable = false, precision = 18, scale = 8)
    val high: BigDecimal,

    @Column(nullable = false, precision = 18, scale = 8)
    val low: BigDecimal,

    @Column(nullable = false, precision = 18, scale = 8)
    val close: BigDecimal,

    @Column(name = "volume")
    val volume: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    val currency: CurrencyType? = null
)