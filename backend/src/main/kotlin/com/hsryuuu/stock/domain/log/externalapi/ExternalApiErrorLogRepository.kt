package com.hsryuuu.stock.domain.log.externalapi

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExternalApiErrorLogRepository : JpaRepository<ExternalApiErrorLog, Long>