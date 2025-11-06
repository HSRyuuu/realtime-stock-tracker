package com.hsryuuu.stock.domain.user.watchlist.repository

import com.hsryuuu.stock.domain.user.member.repository.MemberRepository
import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchGroup
import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchItem
import com.querydsl.jpa.JPQLQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomWatchItemRepository(
    private val queryFactory: JPQLQueryFactory,
    private val watchItemRepository: WatchItemRepository,
    private val watchGroupRepository: WatchGroupRepository,
    private val memberRepository: MemberRepository,
) {

    fun findAllWatchGroups(memberId: Long): List<WatchGroup> {
        return watchGroupRepository.findByMemberIdOrderBySortOrder(memberId)
    }

    fun findWatchItemsByMemberId(memberId: Long): List<WatchItem> {
        val refMember = memberRepository.getReferenceById(memberId)
        return watchItemRepository.findByMember(refMember);
    }

}