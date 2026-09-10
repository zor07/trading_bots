package com.zor07.tradingbot.user.alert

import org.springframework.data.jpa.repository.JpaRepository

interface UserAlertSettingsRepository : JpaRepository<UserAlertSettings, Long> {
    fun findByAlertType(alertType: String): UserAlertSettings?
}
