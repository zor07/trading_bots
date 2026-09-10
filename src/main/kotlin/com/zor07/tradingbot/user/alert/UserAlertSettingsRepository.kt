package com.zor07.tradingbot.user.alert

import org.springframework.data.jpa.repository.JpaRepository

interface UserAlertSettingsRepository : JpaRepository<UserAlertSettings, Long> {
    fun findByUserIdAndAlertType(userId: Long, alertType: String): UserAlertSettings?
}
