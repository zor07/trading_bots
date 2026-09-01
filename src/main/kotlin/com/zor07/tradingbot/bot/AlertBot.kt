package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.TelegramProperties
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class AlertBot(properties: TelegramProperties) : TelegramLongPollingBot(properties.botToken) {

    private val username = properties.botUsername

    override fun getBotUsername(): String = username

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val message = SendMessage().apply {
                chatId = update.message.chatId.toString()
                text = "Бот работает. Алерты в разработке."
            }
            execute(message)
        }
    }
}
