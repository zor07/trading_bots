package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "alerts.lsr")
data class LongShortRatioProperties(
    val accountThreshold: Double = 5.0,
    val positionThreshold: Double = 5.0,
    val interval: String = "PT5M"
)
