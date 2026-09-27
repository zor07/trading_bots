package com.zor07.tradingbot.config

import com.zor07.tradingbot.bot.OiAlertBot
import com.zor07.tradingbot.bot.PriceAlertBot
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotConfig(
    private val priceAlertBot: PriceAlertBot,
    private val oiAlertBot: OiAlertBot
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        val api = TelegramBotsApi(DefaultBotSession::class.java)
        api.registerBot(priceAlertBot)
        api.registerBot(oiAlertBot)
        log.info("Telegram bots registered")
    }
}
