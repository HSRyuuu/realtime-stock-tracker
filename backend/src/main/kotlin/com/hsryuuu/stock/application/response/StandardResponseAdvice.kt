package com.hsryuuu.stock.application.response

import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class StandardResponseAdvice : ResponseBodyAdvice<Any> {

    companion object {
        private const val SPRING_DOC_CLASS_PREFIX = "org.springdoc";
        private const val SPRING_FOX_CLASS_PREFIX = "springfox";
    }

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        val className = returnType.declaringClass.name
        val isSwagger = className.startsWith(SPRING_DOC_CLASS_PREFIX) || className.startsWith(SPRING_FOX_CLASS_PREFIX)
        return !isSwagger
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        // 1. File
        if (selectedContentType.includes(MediaType.APPLICATION_OCTET_STREAM))
            return body
        // 2. 객체가 아닌 경우
        if (!MappingJackson2HttpMessageConverter::class.java.isAssignableFrom(selectedConverterType))
            return body

        // 에러
        if (response is ServletServerHttpResponse) {
            val servletResponse = response.servletResponse
            val status = servletResponse.status
            // 에러 상황
            if (status != HttpStatus.OK.value()) {
                if (body is StandardResponse<*>) {
                    return body;
                }
                val httpStatus = HttpStatus.valueOf(status) ?: HttpStatus.INTERNAL_SERVER_ERROR
                return StandardResponse(httpStatus, body)
            }
        }

        return if (body is StandardResponse<*>) {
            body
        } else {
            StandardResponse(body)
        }
    }
}