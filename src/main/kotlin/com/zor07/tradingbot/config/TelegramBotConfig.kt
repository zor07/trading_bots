package com.zor07.tradingbot.config

import com.zor07.tradingbot.bot.AlertBot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotConfig(private val alertBot: AlertBot) {

    @Bean
    fun telegramBotsApi(): TelegramBotsApi {
        val api = TelegramBotsApi(DefaultBotSession::class.java)
        api.registerBot(alertBot)
        return api
    }
}
