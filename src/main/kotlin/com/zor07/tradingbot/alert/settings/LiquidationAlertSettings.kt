package com.zor07.tradingbot.alert.settings

data class LiquidationAlertSettings(
    val enabled: Boolean = true,
    val minUsdValue: Double
)
