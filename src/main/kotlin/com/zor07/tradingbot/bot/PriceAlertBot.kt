package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.config.properties.TelegramProperties
import com.zor07.tradingbot.user.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultBotOptions

@Component
class PriceAlertBot(
    properties: TelegramProperties,
    appProperties: AppProperties,
    userService: UserService
) : AbstractAlertBot(buildOptions(properties), properties.priceBot.token, appProperties, userService, BotType.PRICE) {

    private val username = properties.priceBot.username

    override fun getBotUsername(): String = username

    companion object {
        private fun buildOptions(properties: TelegramProperties): DefaultBotOptions {
            val options = DefaultBotOptions()
            if (properties.proxyHost.isNotBlank() && properties.proxyPort > 0) {
                options.proxyHost = properties.proxyHost
                options.proxyPort = properties.proxyPort
                options.proxyType = DefaultBotOptions.ProxyType.SOCKS5
            }
            return options
        }
    }
}
