package com.hsryuuu.stock.application.exception

import org.springframework.http.HttpStatus

class GlobalException(
    val status: HttpStatus,
    private val errorMessage: String,
    override val cause: Throwable?,
    val data: Any?
) : RuntimeException(errorMessage, cause) {

    constructor(status: HttpStatus) :
            this(status, status.name, null, null);

    constructor(status: HttpStatus, errorMessage: String) :
            this(status, errorMessage, null, null);
}
