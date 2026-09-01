package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.TelegramProperties
import com.zor07.tradingbot.user.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class AlertBot(
    properties: TelegramProperties,
    private val userService: UserService
) : TelegramLongPollingBot(properties.botToken) {

    private val username = properties.botUsername

    override fun getBotUsername(): String = username

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage() || !update.message.hasText()) return

        val message = update.message
        val chatId = message.chatId
        val text = message.text.trim()

        when {
            text.startsWith("/subscribe") -> handleSubscribe(chatId, message.from?.userName)
        }
    }

    private fun handleSubscribe(chatId: Long, username: String?) {
        val isNew = userService.subscribe(chatId, username)
        val reply = if (isNew) "Вы подписаны на алерты." else "Вы уже подписаны."
        execute(SendMessage(chatId.toString(), reply))
    }
}
