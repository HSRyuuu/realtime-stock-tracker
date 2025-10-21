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
@Table(name = "STOCK_RSI")
data class StockRsi(

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
    val rsi: BigDecimal,  // RSI 지수

    @Column(nullable = false, precision = 18, scale = 8)
    val avgGain: BigDecimal, // 평균 상승폭

    @Column(nullable = false, precision = 18, scale = 8)
    val avgLoss: BigDecimal, // 평균 하락폭

    @Column(nullable = false)
    val period: Int = 14, // 계산 기간

    @Column(nullable = false)
    val date: LocalDate,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime? = LocalDateTime.now()
)