package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.config.properties.TelegramProperties
import com.zor07.tradingbot.user.UserService
import org.springframework.stereotype.Component

@Component
class PriceAlertBot(
    properties: TelegramProperties,
    appProperties: AppProperties,
    userService: UserService
) : AbstractAlertBot(properties, properties.priceBot.token, appProperties, userService, BotType.PRICE) {

    private val username = properties.priceBot.username

    override fun getBotUsername(): String = username
}
