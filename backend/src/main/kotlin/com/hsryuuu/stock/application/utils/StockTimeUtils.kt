package com.hsryuuu.stock.application.utils

import java.time.DayOfWeek
import java.time.LocalDateTime

object StockTimeUtils {

    fun isMarketOpenNow(dateTime: LocalDateTime, zoneId: String): Boolean {
        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zoneId)
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zoneId)

        val nowNewYork = TimeUtils.getZoneDateTime(dateTime, zoneId)
        val dayOfWeek = nowNewYork.dayOfWeek
        val time = nowNewYork.toLocalTime()
        val isTradingDay = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
        val isTradingHours = time.isAfter(tradingStartTime)
                && time.isBefore(tradingEndTime)

        return isTradingDay && isTradingHours
    }
}