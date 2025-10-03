package com.hsryuuu.stock.application.dto

data class ProcessResult<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T): ProcessResult<T> = ProcessResult(true, null, data)
        fun <T> fail(): ProcessResult<T> = ProcessResult(false, null, null)
        fun <T> fail(message: String): ProcessResult<T> = ProcessResult(false, message, null)
        fun <T> error(message: String): ProcessResult<T> = ProcessResult(false, message, null)
    }
}