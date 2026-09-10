package com.zor07.tradingbot.alert.settings

data class LongShortRatioSettings(
    val enabled: Boolean = true,
    val accountThreshold: Double,
    val positionThreshold: Double,
    val requireBoth: Boolean = true
)
