package com.hsryuuu.stock.infra.stockapi.type

enum class StockApiSource(val url: String, val apiUrl: String) {
    TWELVE_DATA("https://twelvedata.com", "https://api.twelvedata.com")
}