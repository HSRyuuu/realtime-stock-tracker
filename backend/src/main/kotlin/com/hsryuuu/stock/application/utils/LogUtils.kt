package com.hsryuuu.stock.application.utils

object LogUtils {
    data class LocationInfo(
        val className: String = "",
        val methodName: String = "",
        val fileName: String = "",
        val lineNumber: Int? = null
    )

    fun currentLocation(
        targetClass: Class<*>,
        depth: Int = 0
    ): LocationInfo {
        val stackTrace = Thread.currentThread().stackTrace

        // 현재 클래스와 일치하는 스택 프레임 찾기
        val element = stackTrace
            .firstOrNull { it.className == targetClass.name }
            ?: return LocationInfo()

        // 필요하면 depth 조정 (호출자 vs 현재 메서드 구분)
        val index = stackTrace.indexOf(element) + depth
        if (index >= stackTrace.size) return LocationInfo()

        val e = stackTrace[index]
        return LocationInfo(
            className = e.className,
            methodName = e.methodName,
            fileName = e.fileName,
            lineNumber = e.lineNumber
        )
    }
}