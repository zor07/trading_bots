package com.zor07.tradingbot.user.alert.settings

data class PriceAlertUserSettings(
    val enabled: Boolean = true,
    val threshold: Double = 0.5,
    val candleInterval: String = "15m",
    val candleLimit: Int = 1,
    val cooldownMinutes: Int = 30,
    val excludedSymbols: List<String> = emptyList()
)
