package com.hsryuuu.stock.infra.stock.param

import com.hsryuuu.stock.domain.stock.model.type.Timeframe

class TwelveDataParameterConverter : ParameterConverter {
    private val unitMapping: Map<Timeframe, String> = mapOf(
        Timeframe.MIN1 to "1m",
        Timeframe.MIN5 to "5m",
        Timeframe.MIN15 to "15m",
        Timeframe.MIN30 to "30m",
        Timeframe.MIN45 to "45m",
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