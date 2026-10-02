package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "alerts.liquidation")
data class LiquidationAlertProperties(
    val minUsdValue: Double = 100_000.0
)
