package com.hsryuuu.stock.domain.stock.model.dto

import com.hsryuuu.stock.domain.stock.model.type.CandleCollectState

data class SymbolStatus(
    val symbol: String,
    val ready: Boolean,
    val state: CandleCollectState,
    val message: String,
)