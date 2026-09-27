package com.zor07.tradingbot.alert.settings

data class OiAlertSettings(
    val enabled: Boolean = true,
    val threshold: Double,
    val period: String
)
