package com.zor07.tradingbot.alert

import com.zor07.tradingbot.bot.AlertBot
import com.zor07.tradingbot.config.properties.TelegramProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class AlertNotifier(
    private val bot: AlertBot,
    private val telegramProperties: TelegramProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun send(message: String) {
        log.info("Sending alert: {}", message)
        val sendMessage = SendMessage().apply {
            chatId = telegramProperties.chatId
            text = message
        }
        bot.execute(sendMessage)
    }
}
