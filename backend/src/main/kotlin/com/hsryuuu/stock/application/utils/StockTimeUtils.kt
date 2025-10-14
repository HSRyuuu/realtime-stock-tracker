package com.hsryuuu.stock.application.utils

import java.time.*

object StockTimeUtils {
    fun isMarketOpenNow(zoneId: String): Boolean {
        val nowInZone = ZonedDateTime.now(ZoneId.of(zoneId))
        return isTradingDay(nowInZone) && isTradingHours(nowInZone)
    }

    fun resolveLastMarketOpenDate(zoneId: String): LocalDate {
        val nowInZone = ZonedDateTime.now(ZoneId.of(zoneId))
        return if (isWeekendCloseTime(nowInZone.toLocalDateTime(), zoneId) || isMonday(nowInZone)) {
            TimeUtils.getLastFriday(nowInZone.toLocalDate())
        } else {
            TimeUtils.getYesterday(nowInZone.toLocalDate())
        }
    }

    private fun isMonday(zonedDateTime: ZonedDateTime): Boolean =
        zonedDateTime.dayOfWeek == DayOfWeek.MONDAY

    private fun isWeekendCloseTime(dateTime: LocalDateTime, zoneId: String): Boolean {
        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zoneId)
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zoneId)
        val dayOfWeek = dateTime.dayOfWeek
        val localTime = dateTime.toLocalTime()

        return when {
            // 금요일 장 마감 이후
            dayOfWeek == DayOfWeek.FRIDAY && !localTime.isBefore(tradingEndTime) -> true
            // 토/일 전체
            dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY -> true
            // 월요일 장 시작 전
            dayOfWeek == DayOfWeek.MONDAY && !localTime.isAfter(tradingStartTime) -> true
            else -> false
        }
    }

    private fun isTradingHours(zonedDateTime: ZonedDateTime): Boolean {
        val time = zonedDateTime.toLocalTime()
        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zonedDateTime.zone.id)
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zonedDateTime.zone.id)
        return !time.isBefore(tradingStartTime) && !time.isAfter(tradingEndTime)
    }

    private fun isTradingDay(zonedDateTime: ZonedDateTime): Boolean {
        val dayOfWeek = zonedDateTime.dayOfWeek
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
    }


}

fun main() {
    val resolveReferenceDate = StockTimeUtils.resolveLastMarketOpenDate(TimeUtils.TIME_ZONE_AMERICA_NEW_YORK)
    println(resolveReferenceDate)
}