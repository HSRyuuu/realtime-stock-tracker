package com.hsryuuu.stock.infra.redis.limit

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class TwelveDataApiRateLimiter(
    private val redisTemplate: StringRedisTemplate
) {
    private val log = LoggerFactory.getLogger(TwelveDataApiRateLimiter::class.java)

    companion object {
        const val DEFAULT_MINUTE_LIMIT = 8;
        const val DEFAULT_DAY_LIMIT = 800;
    }

    /**
     * API 이용 제한 여부 확인
     * 이용가능: TRUE, 이용 불가: FAIL
     */
    fun checkAndIncrement(key: String): Boolean {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val minuteKey = "rate:$key:minute:${now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))}"
        val dayKey = "rate:$key:day:${now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}"

        val minuteCount = redisTemplate.opsForValue().increment(minuteKey) ?: 0
        val dayCount = redisTemplate.opsForValue().increment(dayKey) ?: 0

        redisTemplate.expire(minuteKey, Duration.ofMinutes(1))
        redisTemplate.expire(dayKey, Duration.ofDays(1))

        if (minuteCount > DEFAULT_MINUTE_LIMIT) {
            log.warn("TWELVE DATA : Minute limit exceeded for [$key], count: $minuteCount")
        } else {
            log.info("TWELVE DATA : use api [$key], minute count: $minuteCount, daily count: $dayCount")
        }
        if (dayCount > DEFAULT_DAY_LIMIT) {
            log.warn("TWELVE DATA : Day limit exceeded for [$key], count: $dayCount")
        }

        return !(minuteCount > DEFAULT_MINUTE_LIMIT || dayCount > DEFAULT_DAY_LIMIT)
    }


}