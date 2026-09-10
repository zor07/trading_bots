package com.zor07.tradingbot.alert

import com.zor07.tradingbot.bot.AlertBot
import com.zor07.tradingbot.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class AlertNotifier(
    private val bot: AlertBot,
    private val userService: UserService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun send(message: String) {
        val chatIds = userService.getChatIds()
        if (chatIds.isEmpty()) {
            log.warn("No subscribers, alert not sent: {}", message)
            return
        }
        log.info("Sending alert to {} subscribers: {}", chatIds.size, message)
        chatIds.forEach { chatId ->
            runCatching {
                bot.execute(SendMessage(chatId.toString(), message).also { it.disableWebPagePreview = true })
                log.info("Alert sent to chatId={}", chatId)
            }.onFailure {
                log.error("Failed to send alert to chatId={}: {}", chatId, it.message)
            }
        }
    }
}
