package com.hsryuuu.stock.domain.user.member.controller

import com.hsryuuu.stock.domain.user.member.model.dto.DuplicateCheckResponse
import com.hsryuuu.stock.domain.user.member.model.dto.MemberSignupRequest
import com.hsryuuu.stock.domain.user.member.model.dto.SignupResponse
import com.hsryuuu.stock.domain.user.member.service.MemberService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@Tag(name = "회원 API")
@RequestMapping("/api/members")
@RestController
class MemberController(
    private val memberService: MemberService
) {
    @GetMapping("/check/username")
    fun checkUsernameExists(@RequestParam username: String): DuplicateCheckResponse {
        val exists = memberService.existsByUsername(username)
        return DuplicateCheckResponse(exists)
    }

    @GetMapping("/check/nickname")
    fun checkNicknameExists(@RequestParam nickname: String): DuplicateCheckResponse {
        val exists = memberService.existsByNickname(nickname)
        return DuplicateCheckResponse(exists)
    }

    @GetMapping("/check/email")
    fun checkEmailExists(@RequestParam email: String): DuplicateCheckResponse {
        val exists = memberService.existsByEmail(email)
        return DuplicateCheckResponse(exists)
    }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: MemberSignupRequest): SignupResponse {
        val memberId = memberService.signup(request)
        return SignupResponse(
            id = memberId,
            username = request.username,
            nickname = request.nickname,
            email = request.email
        )
    }

}