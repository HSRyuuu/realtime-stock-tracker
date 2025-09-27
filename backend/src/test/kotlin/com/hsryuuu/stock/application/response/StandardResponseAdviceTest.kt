package com.hsryuuu.stock.application.response


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springdoc.webmvc.api.OpenApiWebMvcResource
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StandardResponseAdviceTest {

    private lateinit var advice: StandardResponseAdvice

    @BeforeEach
    fun setUp() {
        advice = StandardResponseAdvice()
    }

    @Test
    @DisplayName("supports: [Swagger 예외처리] SpringDoc 클래스는 false 반환")
    fun advice_supports_return_false_for_springdoc_class() {
        // given
        val parameter = MethodParameter(
            OpenApiWebMvcResource::class.java.getConstructors().first(),
            -1
        )
        // when
        val result = advice.supports(parameter, MappingJackson2HttpMessageConverter::class.java)
        // then
        assertEquals(false, result)
    }

    @Test
    @DisplayName("supports: [일반 응답] 일반 컨트롤러 클래스는 true 반환")
    fun advice_supports_return_false_for_normal_class() {
        // given
        val parameter = MethodParameter(
            DummyController::class.java.getMethod(DummyController.METHOD_NAME_OBJECT),
            -1
        )
        // when
        val result = advice.supports(parameter, MappingJackson2HttpMessageConverter::class.java)
        // then
        assertEquals(true, result)
    }

    @Test
    @DisplayName("beforeBodyWrite: [파일 응답의 경우] 파일 응답은 그대로 반환")
    fun beforeBodyWrite_dont_wrap_file_response() {
        // given
        val body = byteArrayOf(1, 2, 3)
        val parameter = MethodParameter(
            DummyController::class.java.getMethod(DummyController.METHOD_NAME_FILE),
            -1
        )
        // when
        val result = advice.beforeBodyWrite(
            body,
            parameter,
            MediaType.APPLICATION_OCTET_STREAM,
            MappingJackson2HttpMessageConverter::class.java,
            org.mockito.Mockito.mock(ServerHttpRequest::class.java),
            org.mockito.Mockito.mock(ServerHttpResponse::class.java)
        )
        // then
        assertSame(result, body)
    }

    @Test
    @DisplayName("beforeBodyWrite: [객체가 아닌 경우] 객체가 아닌 타입은 그대로 반환")
    fun beforeBodyWrite_dont_wrap_non_object_type() {
        // given
        val body = "hello"
        val response = ServletServerHttpResponse(MockHttpServletResponse())
        val parameter = MethodParameter(
            DummyController::class.java.getMethod(DummyController.METHOD_NAME_STRING),
            -1
        )
        // when
        val result = advice.beforeBodyWrite(
            body,
            parameter,
            MediaType.APPLICATION_JSON,
            StringHttpMessageConverter::class.java,
            org.mockito.Mockito.mock(org.springframework.http.server.ServerHttpRequest::class.java),
            response
        )
        // then
        assertFalse(result is StandardResponse<*>)
        assertEquals(body, result)
    }

    @Test
    @DisplayName("beforeBodyWrite: [에러 응답] 에러 상태일 경우에도 StandardResponse 형식으로 반환")
    fun beforeBodyWrite_wrap_error_response() {
        // given
        val body = "error-message"
        val mockServletResponse = MockHttpServletResponse().apply {
            status = 500
        }
        val response = ServletServerHttpResponse(mockServletResponse)
        val parameter = MethodParameter(
            DummyController::class.java.getMethod(DummyController.METHOD_NAME_THROW_ERROR),
            -1
        )
        // when
        val result = advice.beforeBodyWrite(
            body,
            parameter,
            MediaType.APPLICATION_JSON,
            MappingJackson2HttpMessageConverter::class.java,
            org.mockito.Mockito.mock(org.springframework.http.server.ServerHttpRequest::class.java),
            response
        )
        // then
        assertTrue(result is StandardResponse<*>)
        val std = result as StandardResponse<*>
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), std.statusCode)
        assertEquals(body, std.data)
    }

    @Test
    @DisplayName("beforeBodyWrite: [객체 반환] 객체 반환은 StandardResponse 로 감싸짐")
    fun beforeBodyWrite_wrap_object_response() {
        // given
        val body = DummyData("key", 1)
        val response = ServletServerHttpResponse(MockHttpServletResponse())

        val parameter = MethodParameter(
            DummyController::class.java.getMethod(DummyController.METHOD_NAME_OBJECT),
            -1
        )
        // when
        val result = advice.beforeBodyWrite(
            body,
            parameter,
            MediaType.APPLICATION_JSON,
            MappingJackson2HttpMessageConverter::class.java,
            org.mockito.Mockito.mock(org.springframework.http.server.ServerHttpRequest::class.java),
            response
        )
        // then
        assertTrue(result is StandardResponse<*>)
        val std = result as StandardResponse<*>
        assertEquals(HttpStatus.OK.value(), std.statusCode)
        assertEquals(body, std.data)
    }

    @Test
    @DisplayName("beforeBodyWrite: [StandardResponse 반환] StandardResponse 를 반환할 때는 wrapping 하지 않고 그대로 반환한다.")
    fun beforeBodyWrite_dont_wrap_double_standard_response() {
        // given
        val dummyData = DummyData("key", 1)
        val body = StandardResponse(data = dummyData)
        val response = ServletServerHttpResponse(MockHttpServletResponse())
        val parameter = MethodParameter(
            DummyController::class.java.getMethod(DummyController.METHOD_NAME_STANDARD_RESPONSE),
            -1
        )
        // when
        val result = advice.beforeBodyWrite(
            body,
            parameter,
            MediaType.APPLICATION_JSON,
            MappingJackson2HttpMessageConverter::class.java,
            org.mockito.Mockito.mock(org.springframework.http.server.ServerHttpRequest::class.java),
            response
        )
        // then
        assertTrue(result is StandardResponse<*>)
        val std = result as StandardResponse<*>
        assertEquals(HttpStatus.OK.value(), std.statusCode)
        assertEquals(body.data, dummyData)
    }


    // Dummy controller for method references
    class DummyController {

        companion object {
            const val METHOD_NAME_FILE = "getFile"
            const val METHOD_NAME_STRING = "getString"
            const val METHOD_NAME_THROW_ERROR = "throwError"
            const val METHOD_NAME_OBJECT = "getObject"
            const val METHOD_NAME_STANDARD_RESPONSE = "getStandardResponse"
        }

        fun getFile(): ByteArray = byteArrayOf(1, 2, 3)
        fun getString(): String = "hello"
        fun throwError(): String = throw RuntimeException("dummy error")
        fun getObject(): DummyData = DummyData("key", 1)
        fun getStandardResponse(): StandardResponse<*> = StandardResponse(data = DummyData("key", 1))
    }

    data class DummyData(val key: String, val value: Int)
}