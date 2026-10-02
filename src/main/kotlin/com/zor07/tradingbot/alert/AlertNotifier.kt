package com.zor07.tradingbot.alert

import com.zor07.tradingbot.bot.BotType
import com.zor07.tradingbot.bot.LiquidationAlertBot
import com.zor07.tradingbot.bot.OiAlertBot
import com.zor07.tradingbot.bot.PriceAlertBot
import com.zor07.tradingbot.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

@Service
class AlertNotifier(
    private val priceBot: PriceAlertBot,
    private val oiBot: OiAlertBot,
    private val liquidationBot: LiquidationAlertBot,
    private val userService: UserService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun send(message: String, botType: BotType) {
        val bot = botFor(botType)
        val chatIds = userService.getChatIds(botType)
        if (chatIds.isEmpty()) {
            log.warn("No subscribers for {}, alert not sent", botType)
            return
        }
        log.info("Sending alert to {} {} subscribers", chatIds.size, botType)
        chatIds.forEach { chatId ->
            runCatching {
                bot.execute(SendMessage(chatId.toString(), message).also {
                    it.disableWebPagePreview = true
                    it.parseMode = "Markdown"
                })
            }.onFailure {
                log.error("Failed to send alert to chatId={}: {}", chatId, it.message)
            }
        }
    }

    fun sendToAll(message: String) {
        BotType.entries.forEach { send(message, it) }
    }

    private fun botFor(botType: BotType): TelegramLongPollingBot = when (botType) {
        BotType.PRICE -> priceBot
        BotType.OI -> oiBot
        BotType.LIQUIDATION -> liquidationBot
    }
}
