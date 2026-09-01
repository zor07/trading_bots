package com.zor07.tradingbot.alert

import org.springframework.data.jpa.repository.JpaRepository

interface AlertHistoryRepository : JpaRepository<AlertHistory, Long> {

    fun findTopByAlertTypeAndSymbolOrderByCreatedAtDesc(
        alertType: String,
        symbol: String
    ): AlertHistory?
}
