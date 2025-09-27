package com.hsryuuu.stock.application.response

import com.hsryuuu.stock.application.type.OperationResult
import org.springframework.http.HttpStatus

data class StandardResponse<T>(
    var result: OperationResult = OperationResult.SUCCESS,
    var statusCode: Int = HttpStatus.OK.value(),
    var message: String = "",
    var data: T? = null
) {

    constructor(data: T?) : this(OperationResult.SUCCESS, data = data)

    constructor(httpStatus: HttpStatus, data: T?) :
            this(OperationResult.ERROR, httpStatus.value(), data = data)
}

