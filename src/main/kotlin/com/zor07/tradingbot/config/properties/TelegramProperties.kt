package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram")
data class TelegramProperties(
    val botToken: String,
    val botUsername: String,
    val proxyHost: String = "",
    val proxyPort: Int = 0
)
