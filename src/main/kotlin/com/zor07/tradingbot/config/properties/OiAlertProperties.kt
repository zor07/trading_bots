package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "alerts.oi")
data class OiAlertProperties(
    val threshold: Double = 5.0,
    val period: String = "5m",
    val interval: Duration = Duration.ofSeconds(60)
)
