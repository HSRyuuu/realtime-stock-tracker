package com.hsryuuu.stock.infra.stock.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.hsryuuu.stock.application.type.CurrencyType
import com.hsryuuu.stock.application.utils.BucketTimeUtils
import com.hsryuuu.stock.domain.stock.model.dto.CandleDto
import com.hsryuuu.stock.domain.stock.model.type.Timeframe
import java.math.BigDecimal

data class TwelveDataTimeSeriesResponse(
    val meta: Meta,
    val values: List<Value>
) {

    data class Meta(
        val symbol: String,
        val interval: String,
        val currency: String,
        @field:JsonProperty("exchange_timezone")
        val exchangeTimezone: String,
        val exchange: String,
        @field:JsonProperty("mic_code")
        val micCode: String,
        val type: String,
    )

    data class Value(
        val datetime: String,
        val open: String,
        val high: String,
        val low: String,
        val close: String,
        val volume: String?
    )

    companion object {

        fun toCandleDto(meta: Meta, value: Value, timeframe: Timeframe): CandleDto {
            // 1. 문자열 → BigDecimal/Long 변환 (안전하게)
            val open = value.open.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val high = value.high.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val low = value.low.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val close = value.close.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val volume = value.volume?.toLongOrNull() ?: 0L
            val currency = CurrencyType.from(meta.currency)

            val bucketStartUtcMillis = BucketTimeUtils.bucketStartUtcMillis(
                rawDateTime = value.datetime,
                exchangeTimezone = meta.exchangeTimezone,
                timeframe = timeframe
            )
            return CandleDto(
                datetime = BucketTimeUtils.toUtcDateTime(bucketStartUtcMillis),
                time = bucketStartUtcMillis,
                open = open,
                high = high,
                low = low,
                close = close,
                volume = volume,
                currency = currency,
            )
        }
    }
}