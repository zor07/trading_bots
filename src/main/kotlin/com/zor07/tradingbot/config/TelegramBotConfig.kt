package com.zor07.tradingbot.config

import com.zor07.tradingbot.bot.AlertBot
import com.zor07.tradingbot.config.properties.TelegramProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.generics.BotSession
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotConfig(
    private val alertBot: AlertBot,
    private val telegramProperties: TelegramProperties
) {

    @Bean
    fun telegramBotsApi(): TelegramBotsApi {
        if (telegramProperties.proxyHost.isNotBlank() && telegramProperties.proxyPort > 0) {
            System.setProperty("https.proxyHost", telegramProperties.proxyHost)
            System.setProperty("https.proxyPort", telegramProperties.proxyPort.toString())
            System.setProperty("socksProxyHost", telegramProperties.proxyHost)
            System.setProperty("socksProxyPort", telegramProperties.proxyPort.toString())
        }
        val api = TelegramBotsApi(DefaultBotSession::class.java)
        api.registerBot(alertBot)
        return api
    }
}
