package com.hsryuuu.stock.infra.stock.response

data class TwelveDataErrorResponse(
    val code: Int,
    val message: String,
    val status: String
) {
}