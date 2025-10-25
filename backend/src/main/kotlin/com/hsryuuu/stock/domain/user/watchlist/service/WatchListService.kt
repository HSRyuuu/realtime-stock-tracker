package com.hsryuuu.stock.domain.user.watchlist.service

import com.hsryuuu.stock.application.exception.GlobalException
import com.hsryuuu.stock.application.security.AuthManager
import com.hsryuuu.stock.domain.stock.model.dto.SymbolWithIndicatorSignals
import com.hsryuuu.stock.domain.stock.repository.CustomIndicatorRepository
import com.hsryuuu.stock.domain.user.watchlist.model.dto.WatchGroupDto
import com.hsryuuu.stock.domain.user.watchlist.repository.CustomWatchListRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WatchListService(
    private val watchListRepository: CustomWatchListRepository,
    private val indicatorRepository: CustomIndicatorRepository,
    private val authManager: AuthManager
) {

    @Transactional(readOnly = true)
    fun getPopularWatchItems(): List<SymbolWithIndicatorSignals> {
        val M7Symbols = listOf("NVDA", "MSFT", "AAPL", "GOOGL", "AMZN", "META", "TSLA")

        val currentPriceWithIndicators =
            indicatorRepository.getCurrentPriceWithIndicators(M7Symbols)

        return currentPriceWithIndicators.map { SymbolWithIndicatorSignals.from(it) }.toList()
    }

    @Transactional(readOnly = true)
    fun getWatchGroups(): List<WatchGroupDto> {
        val memberId = authManager.getCurrentUserId() ?: throw GlobalException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.")
        return watchListRepository.findAllWatchGroups(memberId)
            .map { WatchGroupDto.fromEntity(it) }
            .toList();

    }

    @Transactional(readOnly = true)
    fun getSymbolsWithIndicators() {
        indicatorRepository.getCurrentPriceWithIndicators(listOf("TSLA", "MSFT", "NVDA"))
    }


}