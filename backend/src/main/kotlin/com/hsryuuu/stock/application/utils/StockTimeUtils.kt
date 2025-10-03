package com.hsryuuu.stock.application.utils

import java.time.DayOfWeek
import java.time.LocalDateTime

object StockTimeUtils {
    fun isMarketOpenNow(dateTime: LocalDateTime, zoneId: String): Boolean {
        return isTradingDay(dateTime, zoneId) && isTradingHours(dateTime, zoneId)
    }

    fun isWeekendCloseTime(dateTime: LocalDateTime, zoneId: String): Boolean {
        val targetZoneDateTime = TimeUtils.getZoneDateTime(dateTime, zoneId)
        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zoneId)
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zoneId)
        val dayOfWeek = targetZoneDateTime.dayOfWeek
        val localTime = targetZoneDateTime.toLocalTime()

        return when {
            // 토/일 전체
            dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY -> true
            // 금요일 장 마감 이후
            dayOfWeek == DayOfWeek.FRIDAY && localTime.isAfter(tradingEndTime) -> true
            // 월요일 장 시작 전
            dayOfWeek == DayOfWeek.MONDAY && localTime.isBefore(tradingStartTime) -> true
            else -> false
        }
    }

    private fun isTradingHours(dateTime: LocalDateTime, zoneId: String): Boolean {
        val targetZoneDateTime = TimeUtils.getZoneDateTime(dateTime, zoneId)

        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zoneId)
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zoneId)

        val time = targetZoneDateTime.toLocalTime()
        return time.isAfter(tradingStartTime) && time.isBefore(tradingEndTime)
    }

    private fun isTradingDay(dateTime: LocalDateTime, zoneId: String): Boolean {
        val targetZoneDateTime = TimeUtils.getZoneDateTime(dateTime, zoneId)
        val dayOfWeek = targetZoneDateTime.dayOfWeek
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
    }


}