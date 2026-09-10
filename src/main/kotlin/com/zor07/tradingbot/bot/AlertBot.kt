package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.config.properties.TelegramProperties
import com.zor07.tradingbot.user.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Component
class AlertBot(
    properties: TelegramProperties,
    private val appProperties: AppProperties,
    private val userService: UserService
) : TelegramLongPollingBot(buildOptions(properties), properties.botToken) {

    companion object {
        private fun buildOptions(properties: TelegramProperties): DefaultBotOptions {
            val options = DefaultBotOptions()
            if (properties.proxyHost.isNotBlank() && properties.proxyPort > 0) {
                options.proxyHost = properties.proxyHost
                options.proxyPort = properties.proxyPort
                options.proxyType = DefaultBotOptions.ProxyType.SOCKS5
            }
            return options
        }
    }

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
        val link = "${appProperties.baseUrl}/trading-bots/login?token=$token"
        val button = InlineKeyboardButton("⚙️ Настройки алертов").also { it.url = link }
        val keyboard = InlineKeyboardMarkup(listOf(listOf(button)))
        execute(SendMessage(chatId.toString(), "Добро пожаловать!").also {
            it.replyMarkup = keyboard
            it.disableWebPagePreview = true
        })
    }
}
