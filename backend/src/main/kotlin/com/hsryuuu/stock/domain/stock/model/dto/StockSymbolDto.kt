package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.domain.stock.model.entity.StockSymbol
import com.hsryuuu.stock.domain.stock.model.type.StockType

data class StockSymbolDto(

    val id: Long? = null,
    val symbol: String, // 종목 코드 (AAPL, SPY 등)
    val name: String, // 회사/ETF 이름
    val stockType: StockType, // STOCK / ETF
    val currency: String? = null, // 통화 (USD, KRW 등)
    val exchange: String, // 거래소 (NASDAQ, NYSE 등)
    val micCode: String? = null, // Market Identifier Code
    val country: String? = null,
    val figiCode: String? = null,
    val cfiCode: String? = null,
    val isin: String? = null,
    val cusip: String? = null,
    val metaData: String? = null
) {
    companion object {
        fun from(entity: StockSymbol): StockSymbolDto {
            return StockSymbolDto(
                id = entity.id,
                symbol = entity.symbol,
                name = entity.name,
                stockType = entity.stockType,
                currency = entity.currency,
                exchange = entity.exchange,
                micCode = entity.micCode,
                country = entity.country,
                figiCode = entity.figiCode,
                cfiCode = entity.cfiCode,
                isin = entity.isin,
                cusip = entity.cusip,
                metaData = entity.metaData,
            )
        }
    }
}