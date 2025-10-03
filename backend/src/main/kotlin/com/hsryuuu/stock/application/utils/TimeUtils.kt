package com.hsryuuu.stock.application.utils

import java.time.*
import java.time.temporal.TemporalAdjusters

object TimeUtils {

    const val TIME_ZONE_UTC = "UTC"
    const val TIME_ZONE_AMERICA_NEW_YORK = "America/New_York"
    const val TIME_ZONE_ASIA_SEOUL = "Asia/Seoul"

    fun isWeekend(date: LocalDate = LocalDate.now()): Boolean {
        return date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
    }

    fun getLastFriday(from: LocalDate = LocalDate.now()): LocalDate {
        return from.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
    }

    fun getYesterday(from: LocalDate = LocalDate.now()): LocalDate {
        return from.minusDays(1);
    }

    fun getZoneDateTime(
        dateTime: LocalDateTime,
        zoneId: String? = TIME_ZONE_AMERICA_NEW_YORK
    ): ZonedDateTime {
        val sourceZone = ZoneId.systemDefault()
        val targetZone = ZoneId.of(zoneId)
        return dateTime.atZone(sourceZone).withZoneSameInstant(targetZone)
    }

    fun getZoneEpochMilli(dateTime: LocalDateTime, zoneId: String? = TIME_ZONE_AMERICA_NEW_YORK): Long {
        return getZoneDateTime(dateTime, zoneId).toInstant().toEpochMilli()
    }

}
