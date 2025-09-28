package com.hsryuuu.stock.application.type

data class ProcessResult<T>(
    val result: OperationResult,
    val message: String = "",
    val data: T? = null
) {

    companion object {
        fun <T> defaultSuccess(): ProcessResult<T> =
            ProcessResult(OperationResult.SUCCESS, OperationResult.SUCCESS.name.lowercase())

        fun <T> success(message: String): ProcessResult<T> = ProcessResult(OperationResult.SUCCESS, message)
        fun <T> success(data: T): ProcessResult<T> =
            ProcessResult(OperationResult.SUCCESS, OperationResult.SUCCESS.name.lowercase(), data)

        fun <T> success(message: String, data: T): ProcessResult<T> =
            ProcessResult(OperationResult.SUCCESS, message, data)

        fun <T> defaultError(): ProcessResult<T> =
            ProcessResult(OperationResult.ERROR, OperationResult.ERROR.name.lowercase())

        fun <T> error(message: String): ProcessResult<T> = ProcessResult(OperationResult.ERROR, message)
        fun <T> error(data: T): ProcessResult<T> =
            ProcessResult(OperationResult.ERROR, OperationResult.ERROR.name.lowercase(), data)

        fun <T> error(message: String, data: T): ProcessResult<T> =
            ProcessResult(OperationResult.ERROR, message = message, data = data)
    }


}