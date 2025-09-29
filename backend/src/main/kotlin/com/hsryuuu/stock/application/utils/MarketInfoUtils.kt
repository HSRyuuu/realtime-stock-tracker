package com.hsryuuu.stock.application.utils

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import org.springframework.http.HttpStatus
import java.time.LocalTime

object MarketInfoUtils {

    val US_TRADING_HOURS_START: LocalTime = LocalTime.of(9, 30)
    val US_TRADING_HOURS_END: LocalTime = LocalTime.of(16, 0)
    val KOR_TRADING_HOURS_START: LocalTime = LocalTime.of(9, 0)
    val KOR_TRADING_HOURS_END: LocalTime = LocalTime.of(15, 30)

    fun getTradingStartTime(zone: String): LocalTime =
        when (zone) {
            TimeUtils.TIME_ZONE_AMERICA_NEW_YORK -> US_TRADING_HOURS_START
            TimeUtils.TIME_ZONE_ASIA_SEOUL -> KOR_TRADING_HOURS_START
            else -> throw GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorMessage.BAD_REQUEST_ENUM)
        }

    fun getTradingEndTime(zone: String): LocalTime =
        when (zone) {
            TimeUtils.TIME_ZONE_AMERICA_NEW_YORK -> US_TRADING_HOURS_END
            TimeUtils.TIME_ZONE_ASIA_SEOUL -> KOR_TRADING_HOURS_END
            else -> throw GlobalException(HttpStatus.INTERNAL_SERVER_ERROR, GlobalErrorMessage.BAD_REQUEST_ENUM)

        }
}