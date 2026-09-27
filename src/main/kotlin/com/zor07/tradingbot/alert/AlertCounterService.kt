package com.zor07.tradingbot.alert

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class AlertCounterService {

    private val log = LoggerFactory.getLogger(javaClass)
    private val counters = ConcurrentHashMap<String, AtomicInteger>()

    fun increment(key: String): Int = counters.getOrPut(key) { AtomicInteger(0) }.incrementAndGet()

    @Scheduled(cron = "0 0 0 * * *")
    fun reset() {
        counters.clear()
        log.info("Alert counters reset")
    }
}
