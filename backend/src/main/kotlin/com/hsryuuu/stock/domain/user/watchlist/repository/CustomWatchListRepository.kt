package com.hsryuuu.stock.domain.user.watchlist.repository

import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchGroup
import com.querydsl.jpa.JPQLQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomWatchListRepository(
    private val queryFactory: JPQLQueryFactory,
    private val watchItemRepository: WatchItemRepository,
    private val watchGroupRepository: WatchGroupRepository
) {

    fun findAllWatchGroups(memberId: Long): List<WatchGroup> {
        return watchGroupRepository.findByMemberIdOrderBySortOrder(memberId)
    }

}