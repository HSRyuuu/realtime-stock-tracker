package com.hsryuuu.stock.application.type

import com.hsryuuu.stock.application.exception.GlobalErrorMessage
import com.hsryuuu.stock.application.exception.GlobalException
import org.springframework.http.HttpStatus

enum class YNFlag(val value: Boolean) {
    Y(true),
    N(false);

    companion object {
        fun fromBoolean(value: Boolean): YNFlag = if (value) Y else N

        fun fromString(value: String): YNFlag =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw GlobalException(HttpStatus.BAD_REQUEST, GlobalErrorMessage.BAD_REQUEST_ENUM);

        fun getYesFlags(): List<YNFlag> = listOf(Y)
        fun getNoFlags(): List<YNFlag> = listOf(N)
    }
}