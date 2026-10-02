package com.zor07.tradingbot.alert.liquidation

import com.zor07.tradingbot.alert.AlertMessageBuilder
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.bot.BotType
import com.zor07.tradingbot.market.LiquidationEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class LiquidationAlertEventService(
    private val notifier: AlertNotifier,
    private val settingsService: AlertSettingsService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onLiquidation(event: LiquidationEvent) {
        val settings = settingsService.getLiquidationSettings()
        if (!settings.enabled) return
        if (event.data.usdValue < settings.minUsdValue) return
        val message = AlertMessageBuilder.buildLiquidationMessage(event.symbol, event.data)
        log.info("LIQUIDATION ALERT: {} {} usd={}", event.symbol, event.data.side, event.data.usdValue)
        notifier.send(message, BotType.LIQUIDATION)
    }

}
