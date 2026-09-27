package com.zor07.tradingbot.alert.oi

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.exchange.oi.OpenInterestClient
import com.zor07.tradingbot.user.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class OiAlertScheduler(
    private val oiClients: List<OpenInterestClient>,
    private val alertService: OiAlertService,
    private val userService: UserService,
    private val settingsService: AlertSettingsService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${alerts.oi.interval}")
    fun run() {
        val settings = settingsService.getOiSettings()
        val symbols = settingsService.getWatchlist()
        val subscriberCount = userService.getChatIds().size

        log.info("OI alert tick: {} symbols, {} clients, {} subscribers", symbols.size, oiClients.size, subscriberCount)

        if (subscriberCount == 0) {
            log.warn("No subscribers — OI alerts will not be sent")
            return
        }

        if (!settings.enabled) {
            log.info("OI alerts disabled in settings — skipping tick")
            return
        }

        runBlocking {
            symbols.map { symbol ->
                async(Dispatchers.IO) {
                    val values = oiClients.mapNotNull { client ->
                        runCatching { client.getOpenInterestHistory(symbol, settings.period) }
                            .onFailure { log.warn("Failed to get OI for {} from {}: {}", symbol, client.exchangeName, it.message) }
                            .getOrNull()
                            ?.takeIf { it.size >= 2 }
                    }

                    if (values.isEmpty()) return@async

                    val prev = values.mapNotNull { it.firstOrNull() }.average()
                    val curr = values.mapNotNull { it.lastOrNull() }.average()

                    if (prev == 0.0) return@async

                    val changePercent = (curr - prev) / prev * 100
                    log.info("{} OI change={}%", symbol, String.format("%.2f", changePercent))

                    if (abs(changePercent) >= settings.threshold) {
                        log.info("OI ALERT triggered: {} change={}% threshold={}%", symbol, String.format("%.2f", changePercent), settings.threshold)
                        alertService.handle(symbol, changePercent, prev, curr)
                    }
                }
            }.awaitAll()
        }
    }
}
