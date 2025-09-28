package com.hsryuuu.stock.application.utils

import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object BucketTimeUtils {

    /**
     * 시간 정보를 EpochMilli 로 변환 (Bucket 기준 내림)
     */
    fun bucketStartUtcMillis(
        rawDateTime: String,                 //  'datetime' (e.g. "2025-09-26" or "2025-09-26 09:30:02")
        exchangeTimezone: String,              // meta.exchange_timezone (e.g. "America/New_York")
        timeframe: Timeframe
    ): Long {
        val zone = ZoneId.of(exchangeTimezone)

        val normalized = rawDateTime.trim()
        // 입력 포맷 유연 처리 (일봉/분봉)
        val zdt = if (normalized.length == 10) { // "yyyy-MM-dd"
            LocalDate.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay(zone)
        } else {
            LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(zone)
        }

        // 거래소 타임존 기준으로 버킷 시작으로 내림
        val floored = when (timeframe.unit) {
            ChronoUnit.MINUTES -> zdt.truncatedTo(ChronoUnit.MINUTES)
                .withSecond(0).withNano(0)
                .minusMinutes(zdt.minute % timeframe.step)

            ChronoUnit.HOURS -> zdt.truncatedTo(ChronoUnit.HOURS)
                .withMinute(0).withSecond(0).withNano(0)
                .minusHours(zdt.hour % timeframe.step)

            ChronoUnit.DAYS -> zdt.toLocalDate().atStartOfDay(zone)
            ChronoUnit.WEEKS -> zdt.with(java.time.DayOfWeek.MONDAY).toLocalDate().atStartOfDay(zone)
            else -> zdt // 필요 시 추가
        }

        return floored.toInstant().toEpochMilli()
    }

    // millis → UTC LocalDateTime
    fun toUtcDateTime(millis: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("UTC"))

    // millis → 특정 타임존 LocalDateTime
    fun toLocalDateTime(millis: Long, zoneId: String): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of(zoneId))

    // millis → UTC LocalDate
    fun toUtcDate(millis: Long): LocalDate =
        toUtcDateTime(millis).toLocalDate()

}