package com.zor07.tradingbot.bot

import com.zor07.tradingbot.config.properties.AppProperties
import com.zor07.tradingbot.user.UserService
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

abstract class AbstractAlertBot(
    options: DefaultBotOptions,
    token: String,
    private val appProperties: AppProperties,
    private val userService: UserService,
    private val botType: BotType
) : TelegramLongPollingBot(options, token) {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onRegister() {
        runCatching {
            execute(SetMyCommands(listOf(BotCommand("start", "Подписаться и получить ссылку на настройки")), null, null))
        }.onFailure {
            log.warn("Failed to set bot commands: {}", it.message)
        }
    }

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage() || !update.message.hasText()) return
        val message = update.message
        if (message.text.trim().startsWith("/start")) {
            handleStart(message.chatId, message.from?.userName)
        }
    }

    private fun handleStart(chatId: Long, username: String?) {
        userService.subscribe(chatId, username, botType)
        val token = userService.generateToken(chatId)
        val link = "${appProperties.baseUrl}/login?token=$token"
        val isLocal = appProperties.baseUrl.contains("localhost")
        execute(SendMessage(chatId.toString(), if (isLocal) "Добро пожаловать!\n\n$link" else "Добро пожаловать!").also {
            it.disableWebPagePreview = true
            if (!isLocal) {
                val button = InlineKeyboardButton("⚙️ Настройки алертов").also { btn -> btn.url = link }
                it.replyMarkup = InlineKeyboardMarkup(listOf(listOf(button)))
            }
        })
    }
}
