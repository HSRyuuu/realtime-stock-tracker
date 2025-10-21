package com.hsryuuu.stock.domain.stock.model.entity

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@Entity
@IdClass(StockCandleId::class)
@Table(name = "STOCK_BOLLINGER_BAND")
data class BollingerBand(

    @Id
    @Column(nullable = false, length = 50)
    val symbol: String,

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val timeframe: Timeframe,

    @Id
    @Column(nullable = false)
    val bucketStartUtc: Long,

    @Column(nullable = false, precision = 18, scale = 8)
    val middle: BigDecimal,  // 중심선 (SMA)

    @Column(nullable = false, precision = 18, scale = 8)
    val upper: BigDecimal, // 상단 밴드

    @Column(nullable = false, precision = 18, scale = 8)
    val lower: BigDecimal, // 하단 밴드

    @Column(name = "standard_deviation", nullable = false, precision = 18, scale = 8)
    val standardDeviation: BigDecimal,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false)
    val period: Int = 20,

    @Column(name = "k_value", nullable = false, precision = 5, scale = 2)
    val kValue: BigDecimal = BigDecimal("2.00"),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = LocalDateTime.now()
)