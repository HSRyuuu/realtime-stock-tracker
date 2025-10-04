package com.hsryuuu.stock.domain.market.model

import java.time.LocalDateTime

data class ExchangeRateDto(
    val baseCurrency: String,           // 기준 통화 (예: USD)
    val quoteCurrency: String,          // 상대 통화 (예: KRW)
    val rate: Double,               // (예: 1 USD = ? KRW)
    val lastUpdated: LocalDateTime // 갱신 시각 (UTC 기준 추천)
)