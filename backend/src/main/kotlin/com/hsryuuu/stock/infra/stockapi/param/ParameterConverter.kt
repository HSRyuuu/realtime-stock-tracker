package com.hsryuuu.stock.infra.stockapi.param

import com.hsryuuu.stock.domain.stock.model.type.Timeframe


interface ParameterConverter {
    fun interval(timeframe: Timeframe): String
}