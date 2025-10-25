package com.hsryuuu.stock.domain.user.watchlist.model.dto

import com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchGroup
import java.time.LocalDateTime

data class WatchGroupDto(
    val id: Long?,
    val memberId: Long,
    val groupName: String,
    val symbols: List<WatchItemDto> = emptyList(),
    val sortOrder: Int?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(entity: WatchGroup): WatchGroupDto {
            return WatchGroupDto(
                id = entity.id,
                memberId = entity.memberId,
                groupName = entity.groupName,
                sortOrder = entity.sortOrder,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
