package com.zor07.tradingbot.alert.lsr

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.exchange.lsr.LongShortRatioClient
import com.zor07.tradingbot.exchange.lsr.LongShortRatioSnapshot
import com.zor07.tradingbot.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.sign

@Component
class LongShortRatioScheduler(
    private val lsrClients: List<LongShortRatioClient>,
    private val detector: LongShortRatioDetector,
    private val alertService: LongShortRatioAlertService,
    private val userService: UserService,
    private val settingsService: AlertSettingsService
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val previousSnapshots = ConcurrentHashMap<String, LongShortRatioSnapshot>()

    @Scheduled(fixedDelayString = "\${alerts.lsr.interval}")
    fun run() {
        val settings = settingsService.getLsrSettings()
        val symbols = settingsService.getWatchlist()
        val subscriberCount = userService.getChatIds().size

        log.info("LSR alert tick: {} symbols, {} clients, {} subscribers", symbols.size, lsrClients.size, subscriberCount)

        if (subscriberCount == 0) {
            log.warn("No subscribers — LSR alerts will not be sent")
            return
        }

        if (!settings.enabled) {
            log.info("LSR alerts disabled in settings — skipping tick")
            return
        }

        for (symbol in symbols) {
            val accountRatios = lsrClients.mapNotNull { client ->
                runCatching { client.getLsrByAccounts(symbol) }
                    .onFailure { log.warn("Failed to get LSR accounts for {} from {}: {}", symbol, client.exchangeName, it.message) }
                    .getOrNull()
            }

            val positionRatios = lsrClients.mapNotNull { client ->
                runCatching { client.getLsrByPositions(symbol) }
                    .onFailure { log.warn("Failed to get LSR positions for {} from {}: {}", symbol, client.exchangeName, it.message) }
                    .getOrNull()
            }

            if (accountRatios.isEmpty() || positionRatios.isEmpty()) {
                log.debug("Insufficient LSR data for {} — skipping", symbol)
                continue
            }

            val curr = LongShortRatioSnapshot(
                symbol = symbol,
                accountLongRatio = accountRatios.average(),
                positionLongRatio = positionRatios.average()
            )

            val prev = previousSnapshots[symbol]
            previousSnapshots[symbol] = curr

            if (prev == null) {
                log.debug("First LSR snapshot for {} — skipping delta check", symbol)
                continue
            }

            val change = detector.detect(prev, curr)
            log.info("{} LSR accountDelta={}% positionDelta={}%",
                symbol,
                String.format("%.2f", change.accountDelta),
                String.format("%.2f", change.positionDelta)
            )

            val accountTriggered = abs(change.accountDelta) >= settings.accountThreshold
            val positionTriggered = abs(change.positionDelta) >= settings.positionThreshold
            val sameDirection = change.accountDelta.sign == change.positionDelta.sign

            if (accountTriggered && positionTriggered && sameDirection) {
                log.info("LSR ALERT triggered: {} accountDelta={}% positionDelta={}%",
                    symbol,
                    String.format("%.2f", change.accountDelta),
                    String.format("%.2f", change.positionDelta)
                )
                alertService.handle(symbol, change.accountDelta, change.positionDelta)
            }
        }
    }
}
