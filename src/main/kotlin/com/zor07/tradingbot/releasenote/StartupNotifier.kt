package com.zor07.tradingbot.releasenote

import com.zor07.tradingbot.alert.AlertNotifier
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupNotifier(
    private val repository: ReleaseNoteRepository,
    private val notifier: AlertNotifier
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        val unsent = repository.findAllBySentFalse()
        if (unsent.isEmpty()) return

        runCatching {
            val body = unsent.joinToString("\n") { "• ${it.message}" }
            notifier.send("🚀 Обновление задеплоено:\n\n$body")
            unsent.forEach { it.sent = true }
            repository.saveAll(unsent)
            log.info("Release notes sent and marked: ids={}", unsent.map { it.id })
        }.onFailure {
            log.error("Failed to send release notes: {}", it.message)
        }
    }
}
