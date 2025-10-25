package com.hsryuuu.stock.domain.user.watchlist.model.dto

// WatchItem 엔티티를 위한 간단한 DTO도 필요합니다
data class WatchItemDto(
    val id: Long?,
    val symbol: String,
    val alias: String?,
    val sortOrder: Int?
) {
    companion object {
        fun fromEntity(entity: com.hsryuuu.stock.domain.user.watchlist.model.entity.WatchItem): WatchItemDto {
            return WatchItemDto(
                id = entity.id,
                symbol = entity.symbol,
                alias = entity.alias,
                sortOrder = entity.sortOrder
            )
        }
    }
}