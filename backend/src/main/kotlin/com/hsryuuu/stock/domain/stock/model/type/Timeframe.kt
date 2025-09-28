package com.hsryuuu.stock.domain.stock.model.type

import java.time.temporal.ChronoUnit

enum class Timeframe(val code: String, val unit: ChronoUnit, val step: Long) {
    MIN1("1m", ChronoUnit.MINUTES, 1),
    MIN5("5m", ChronoUnit.MINUTES, 5),
    MIN15("15m", ChronoUnit.MINUTES, 15),
    MIN30("30m", ChronoUnit.MINUTES, 30),
    MIN45("45m", ChronoUnit.MINUTES, 45),
    HOUR1("1h", ChronoUnit.HOURS, 1),
    HOUR2("2h", ChronoUnit.HOURS, 2),
    HOUR4("4h", ChronoUnit.HOURS, 4),
    DAY1("1day", ChronoUnit.DAYS, 1),
    WEEK1("1week", ChronoUnit.WEEKS, 1),
    MONTH1("1month", ChronoUnit.MONTHS, 1);    // month는 길이가 가변이라 별도 처리 권장
}