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

        unsent.forEach { note ->
            runCatching {
                notifier.send("🚀 Обновление задеплоено:\n\n${note.message}")
                note.sent = true
                repository.save(note)
                log.info("Release note sent and marked: id={}", note.id)
            }.onFailure {
                log.error("Failed to send release note id={}: {}", note.id, it.message)
            }
        }
    }
}
