package com.hsryuuu.stock.infra.redis.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class StringRedisUtils(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    /** 문자열 저장 */
    fun set(key: String, value: String, ttl: Duration? = null) {
        redisTemplate.opsForValue().set(key, value)
        ttl?.let { redisTemplate.expire(key, it) }
    }

    /** 객체 저장 (JSON 직렬화) */
    fun <T> setObject(key: String, value: T, ttl: Duration? = null) {
        val json = objectMapper.writeValueAsString(value)
        set(key, json, ttl)
    }

    /** 문자열 조회 */
    fun get(key: String): String? = redisTemplate.opsForValue().get(key)

    /** 객체 조회 (JSON 역직렬화) */
    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val json = redisTemplate.opsForValue().get(key) ?: return null
        return objectMapper.readValue(json, clazz)
    }

    /** 존재 여부 */
    fun exists(key: String): Boolean = redisTemplate.hasKey(key)

    /** 삭제 */
    fun delete(key: String): Boolean = redisTemplate.delete(key)

    /** TTL 조회 */
    fun ttl(key: String): Long = redisTemplate.getExpire(key)
}