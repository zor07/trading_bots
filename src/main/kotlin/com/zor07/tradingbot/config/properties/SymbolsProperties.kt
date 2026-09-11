package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "alerts.symbols")
data class SymbolsProperties(
    val limit: Int = 20,
    val updateInterval: Duration = Duration.ofDays(1)
)
