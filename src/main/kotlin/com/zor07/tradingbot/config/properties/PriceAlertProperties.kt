package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "alerts.price")
data class PriceAlertProperties(
    val interval: Duration = Duration.ofSeconds(60),
    val candleInterval: String = "15m",
    val candleLimit: Int = 3,
    val threshold: Double = 2.5
)
