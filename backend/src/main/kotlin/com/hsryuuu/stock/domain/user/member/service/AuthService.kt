package com.hsryuuu.stock.domain.user.member.service

import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.security.AuthManager
import com.hsryuuu.stock.application.security.JwtTokenProvider
import com.hsryuuu.stock.application.security.UserInfo
import com.hsryuuu.stock.domain.user.auth.model.LoginRequest
import com.hsryuuu.stock.domain.user.auth.model.LoginResponse
import com.hsryuuu.stock.domain.user.member.MemberRepository
import com.hsryuuu.stock.domain.user.member.model.dto.MemberSignupRequest
import com.hsryuuu.stock.domain.user.member.model.entity.Member
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authManager: AuthManager
) {

    fun existsByUsername(username: String): Boolean {
        return memberRepository.existsByUsername(username)
    }

    fun existsByNickname(nickname: String): Boolean {
        return memberRepository.existsByNickname(nickname)
    }

    fun existsByEmail(email: String): Boolean {
        return memberRepository.existsByEmail(email)
    }

    @Transactional
    fun signup(request: MemberSignupRequest): Long {
        // 중복 체크
        if (existsByUsername(request.username)) {
            throw IllegalArgumentException("이미 사용중인 아이디입니다.")
        }
        if (existsByNickname(request.nickname)) {
            throw IllegalArgumentException("이미 사용중인 닉네임입니다.")
        }

        if (existsByEmail(request.email)) {
            throw IllegalArgumentException("이미 사용중인 이메일입니다.")
        }


        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

        // 회원 저장
        val member = Member(
            username = request.username,
            password = encodedPassword,
            nickname = request.nickname,
            email = request.email
        )

        val savedMember = memberRepository.save(member)
        return savedMember.id ?: throw IllegalStateException("회원 저장에 실패했습니다.")
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val member = memberRepository.findByUsername(request.username)
            ?: throw GlobalException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다.")
        if (!passwordEncoder.matches(request.password, member.password)) {
            throw GlobalException(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.")
        }
        val userInfo = UserInfo.from(member)
        return LoginResponse(jwtTokenProvider.createToken(userInfo), userInfo)
    }

    fun logout(httpServletRequest: HttpServletRequest) {
        val accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)
        val userInfo = authManager.getCurrentUser()

    }


}