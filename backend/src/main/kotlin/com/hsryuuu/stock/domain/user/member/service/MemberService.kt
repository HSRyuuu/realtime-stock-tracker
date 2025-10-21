package com.hsryuuu.stock.domain.user.member.service

import com.hsryuuu.stock.domain.user.member.MemberRepository
import com.hsryuuu.stock.domain.user.member.model.dto.MemberSignupRequest
import com.hsryuuu.stock.domain.user.member.model.entity.Member
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun existsByUsername(username: String): Boolean {
        return memberRepository.existsByUsername(username)
    }

    fun existsByNickname(nickname: String): Boolean {
        return memberRepository.existsByNickname(nickname)
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

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(request.password)

        // 회원 저장
        val member = Member(
            username = request.username,
            password = encodedPassword,
            nickname = request.nickname,
            phoneNumber = request.phoneNumber
        )

        val savedMember = memberRepository.save(member)
        return savedMember.id ?: throw IllegalStateException("회원 저장에 실패했습니다.")
    }


}