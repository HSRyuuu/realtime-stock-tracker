package com.hsryuuu.stock.domain.user.auth

import com.hsryuuu.stock.domain.user.auth.model.LoginRequest
import com.hsryuuu.stock.domain.user.auth.model.LoginResponse
import com.hsryuuu.stock.domain.user.auth.model.LogoutResponse
import com.hsryuuu.stock.domain.user.member.model.dto.DuplicateCheckResponse
import com.hsryuuu.stock.domain.user.member.model.dto.MemberSignupRequest
import com.hsryuuu.stock.domain.user.member.model.dto.SignupResponse
import com.hsryuuu.stock.domain.user.member.service.AuthService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "인증/인가 API")
@RequestMapping("/api/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {
    @GetMapping("/check/username")
    fun checkUsernameExists(@RequestParam username: String): DuplicateCheckResponse {
        val exists = authService.existsByUsername(username)
        return DuplicateCheckResponse(exists)
    }

    @GetMapping("/check/nickname")
    fun checkNicknameExists(@RequestParam nickname: String): DuplicateCheckResponse {
        val exists = authService.existsByNickname(nickname)
        return DuplicateCheckResponse(exists)
    }

    @GetMapping("/check/email")
    fun checkEmailExists(@RequestParam email: String): DuplicateCheckResponse {
        val exists = authService.existsByEmail(email)
        return DuplicateCheckResponse(exists)
    }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: MemberSignupRequest): SignupResponse {
        val memberId = authService.signup(request)
        return SignupResponse(
            id = memberId,
            username = request.username,
            nickname = request.nickname,
            email = request.email
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return authService.login(request)
    }

    @GetMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest): LogoutResponse {
        // logout
        authService.logout(httpServletRequest)
        return LogoutResponse(true)
    }
}