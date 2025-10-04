package com.hsryuuu.stock.domain.stock.model.dto

import java.time.LocalDateTime

data class StockPriceInfo(
    val symbol: String,                    // 종목 코드 (예: AAPL, TSLA)
    val currentPrice: Double,              // 현재 주가
    val previousClose: Double,             // 전일 종가
    val change: Double,                    // 전일 대비 변동 금액
    val changePercent: Double,             // 전일 대비 변동률 (%)
    val volume: Long,                      // 거래량
    val marketCap: Long,                   // 시가총액
    val high52Week: Double,                // 52주 최고가
    val low52Week: Double,                 // 52주 최저가
    val lastUpdated: LocalDateTime,        // 데이터 업데이트 시각
    val currency: String                   // 통화 (예: USD, KRW)
)