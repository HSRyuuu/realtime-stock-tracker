package com.hsryuuu.stock.infra.stockapi.param

import com.hsryuuu.stock.domain.stock.model.type.Timeframe

class TwelveDataParameterConverter : ParameterConverter {
    private val unitMapping: Map<Timeframe, String> = mapOf(
        Timeframe.MIN1 to "1min",
        Timeframe.MIN5 to "5min",
        Timeframe.MIN15 to "15min",
        Timeframe.MIN30 to "30min",
        Timeframe.MIN45 to "45min",
        Timeframe.HOUR1 to "1h",
        Timeframe.HOUR2 to "2h",
        Timeframe.HOUR4 to "4h",
        Timeframe.DAY1 to "1day",
        Timeframe.WEEK1 to "1week",
        Timeframe.MONTH1 to "1month"
    )

    override fun interval(timeframe: Timeframe): String {
        return unitMapping[timeframe] ?: throw IllegalArgumentException("Unsupported Timeframe: ${timeframe.name}")
    }
}