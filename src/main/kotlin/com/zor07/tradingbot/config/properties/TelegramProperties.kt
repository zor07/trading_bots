package com.zor07.tradingbot.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "telegram")
data class TelegramProperties(
    val priceBot: BotConfig,
    val oiBot: BotConfig,
    val proxyHost: String = "",
    val proxyPort: Int = 0
) {
    data class BotConfig(val token: String, val username: String)
}
