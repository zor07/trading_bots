package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.config.properties.TelegramProperties
import com.zor07.tradingbot.user.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class AlertBot(
    properties: TelegramProperties,
    private val appProperties: AppProperties,
    private val userService: UserService
) : TelegramLongPollingBot(properties.botToken) {

    private val username = properties.botUsername

    override fun getBotUsername(): String = username

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage() || !update.message.hasText()) return
        val message = update.message
        if (message.text.trim().startsWith("/start")) {
            handleStart(message.chatId, message.from?.userName)
        }
    }

    private fun handleStart(chatId: Long, username: String?) {
        userService.subscribe(chatId, username)
        val token = userService.generateToken(chatId)
        val link = "${appProperties.baseUrl}/login?token=$token"
        execute(SendMessage(chatId.toString(), "Добро пожаловать!\n\nНастройте алерты:\n[$link]"))
    }
}
