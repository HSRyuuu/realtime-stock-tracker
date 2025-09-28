package com.hsryuuu.stock.application.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object TimeUtils {
    fun isWeekend(date: LocalDate = LocalDate.now()): Boolean {
        return date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
    }

    fun getLastFriday(from: LocalDate = LocalDate.now()): LocalDate {
        return from.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
    }

    fun getYesterday(from: LocalDate = LocalDate.now()): LocalDate {
        return from.minusDays(1);
    }
}
