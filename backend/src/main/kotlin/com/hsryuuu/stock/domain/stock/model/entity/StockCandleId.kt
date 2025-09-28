package com.hsryuuu.stock.domain.stock.model.entity

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import java.io.Serializable

data class StockCandleId(
    val symbol: String = "",
    val timeframe: Timeframe = Timeframe.DAY1,
    val bucketStartUtc: Long = 0
) : Serializable