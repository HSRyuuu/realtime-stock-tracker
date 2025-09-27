package com.hsryuuu.stock.application.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@EnableJpaAuditing
@Configuration
class JpaAuditConfig : AuditorAware<UUID> {
    override fun getCurrentAuditor(): Optional<UUID> {
        return Optional.of(UUID.randomUUID())
    }
}