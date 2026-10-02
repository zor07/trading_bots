package com.zor07.tradingbot.alert.oi

import com.zor07.tradingbot.alert.AlertCounterService
import com.zor07.tradingbot.alert.AlertMessageBuilder
import com.zor07.tradingbot.alert.AlertNotifier
import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.bot.BotType
import com.zor07.tradingbot.market.SymbolDeltaEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class OiAlertEventService(
    private val settingsService: AlertSettingsService,
    private val notifier: AlertNotifier,
    private val counterService: AlertCounterService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onDelta(event: SymbolDeltaEvent) {
        val delta = event.delta
        val prev = delta.prev.oi ?: return
        val curr = delta.curr.oi ?: return

        if (prev.value == 0.0) return

        val settings = settingsService.getOiSettings()
        if (!settings.enabled) return

        val changePercent = (curr.value - prev.value) / prev.value * 100
        log.info("{} OI change={}%", delta.symbol, String.format("%.2f", changePercent))

        if (changePercent < settings.threshold) return

        val count = counterService.increment("OI:${delta.symbol}")
        val message = AlertMessageBuilder.buildOiMessage(delta.symbol, changePercent, prev.value, curr.value, count, settings)

        log.info("OI ALERT: {} change={}% threshold={}%", delta.symbol, String.format("%.2f", changePercent), settings.threshold)
        notifier.send(message, BotType.OI)
    }

}
