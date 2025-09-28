package com.hsryuuu.stock.domain.stock.model.entity

import com.hsryuuu.stock.domain.stock.model.type.StockType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "STOCK_SYMBOL",
    uniqueConstraints = [UniqueConstraint(columnNames = ["symbol", "exchange"])]
)
data class StockSymbol(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 50)
    val symbol: String, // 종목 코드 (AAPL, SPY 등)

    @Column(nullable = false, length = 500)
    val name: String, // 회사/ETF 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_type", nullable = false, length = 20)
    val stockType: StockType, // STOCK / ETF

    @Column(length = 20)
    val currency: String? = null, // 통화 (USD, KRW 등)

    @Column(nullable = false, length = 100)
    val exchange: String, // 거래소 (NASDAQ, NYSE 등)

    @Column(name = "mic_code", length = 50)
    val micCode: String? = null, // Market Identifier Code

    @Column(length = 200)
    val country: String? = null,

    @Column(name = "figi_code", length = 100)
    val figiCode: String? = null,

    @Column(name = "cfi_code", length = 100)
    val cfiCode: String? = null,

    @Column(length = 100)
    val isin: String? = null,

    @Column(length = 100)
    val cusip: String? = null,

    @Lob
    @Column(name = "meta_data")
    val metaData: String? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    val updatedAt: LocalDateTime? = null
)