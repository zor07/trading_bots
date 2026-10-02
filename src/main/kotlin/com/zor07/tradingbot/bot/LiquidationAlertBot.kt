package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.config.properties.TelegramProperties
import com.zor07.tradingbot.user.UserService
import org.springframework.stereotype.Component

@Component
class LiquidationAlertBot(
    properties: TelegramProperties,
    appProperties: AppProperties,
    userService: UserService
) : AbstractAlertBot(properties, properties.liquidationBot.token, appProperties, userService, BotType.LIQUIDATION) {

    private val username = properties.liquidationBot.username

    override fun getBotUsername(): String = username
}
