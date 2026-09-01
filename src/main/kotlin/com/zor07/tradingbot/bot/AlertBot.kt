package com.zor07.tradingbot.bot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class AlertBot(
    @Value("\${telegram.bot-token}") botToken: String,
    @Value("\${telegram.bot-username}") private val botUsername: String
) : TelegramLongPollingBot(botToken) {

    override fun getBotUsername(): String = botUsername

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
