package com.hsryuuu.stock.application.exception

object GlobalErrorMessage {

    // 기본 에러 메시지
    const val BAD_REQUEST = "Bad Request"
    const val INTERNAL_SERVER_ERROR = "Internal Server Error"
    const val BAD_REQUEST_ENUM = "잘못된 enum 값 요청입니다."
    const val RESOURCE_NOT_FOUND = "존재하지 않는 데이터 입니다."

    @JvmStatic
    fun resourceNotFound(resourceName: String): String =
        "$resourceName 이(가) 존재하지 않습니다."
}