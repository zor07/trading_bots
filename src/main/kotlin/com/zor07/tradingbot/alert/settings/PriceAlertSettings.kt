package com.zor07.tradingbot.alert.settings

data class PriceAlertSettings(
    val enabled: Boolean = true,
    val threshold: Double,
    val candleInterval: String,
    val candleLimit: Int
)
