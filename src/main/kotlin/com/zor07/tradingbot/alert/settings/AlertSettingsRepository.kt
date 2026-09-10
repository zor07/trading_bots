package com.zor07.tradingbot.alert.settings

import org.springframework.data.jpa.repository.JpaRepository

interface AlertSettingsRepository : JpaRepository<AlertSettings, Long> {
    fun findByAlertType(alertType: String): AlertSettings?
}
