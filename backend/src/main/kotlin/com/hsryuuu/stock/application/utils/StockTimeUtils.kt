package com.hsryuuu.stock.application.utils

import com.hsryuuu.stock.application.utils.TimeUtils.getLastFriday
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

object StockTimeUtils {
    fun isMarketOpenNow(zoneId: String): Boolean {
        val nowInZone = ZonedDateTime.now(ZoneId.of(zoneId))
        return isTradingDay(nowInZone) && isTradingHours(nowInZone)
    }

    fun resolveLastMarketOpenDate(zoneId: String): LocalDate {
        val nowInZone = ZonedDateTime.now(ZoneId.of(zoneId))
        val dayOfWeek = nowInZone.dayOfWeek
        val time = nowInZone.toLocalTime()
        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zoneId)
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zoneId)

        return when {
            // 주말인 경우 → 지난 금요일
            dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY ->
                getLastFriday(nowInZone.toLocalDate())

            // 월요일 장 시작 전 (아직 장 안열림) → 지난 금요일
            dayOfWeek == DayOfWeek.MONDAY && time.isBefore(tradingStartTime) ->
                getLastFriday(nowInZone.toLocalDate())

            // 월요일 장 중/장 마감 이후 → 오늘 (월요일)
            dayOfWeek == DayOfWeek.MONDAY ->
                nowInZone.toLocalDate()

            // 화~목, 장 시작 전 → 어제 (이전 거래일)
            dayOfWeek in DayOfWeek.TUESDAY..DayOfWeek.THURSDAY && time.isBefore(tradingStartTime) ->
                nowInZone.toLocalDate().minusDays(1)

            // 금요일 장 시작 전 → 어제 (목요일)
            dayOfWeek == DayOfWeek.FRIDAY && time.isBefore(tradingStartTime) ->
                nowInZone.toLocalDate().minusDays(1)

            // 평일(월~금) 장 중이거나 장 마감 후 → 오늘
            else -> nowInZone.toLocalDate()
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

    fun isAfterClose(zonedDateTime: ZonedDateTime): Boolean {
        val time = zonedDateTime.toLocalTime()
        val tradingEndTime = MarketInfoUtils.getTradingEndTime(zonedDateTime.zone.id)
        return time.isAfter(tradingEndTime)
    }

    fun isBeforeOpen(zonedDateTime: ZonedDateTime): Boolean {
        val time = zonedDateTime.toLocalTime()
        val tradingStartTime = MarketInfoUtils.getTradingStartTime(zonedDateTime.zone.id)
        return time.isBefore(tradingStartTime)
    }


}

fun main() {
    val resolveReferenceDate = StockTimeUtils.resolveLastMarketOpenDate(TimeUtils.TIME_ZONE_AMERICA_NEW_YORK)
    println(resolveReferenceDate)
}