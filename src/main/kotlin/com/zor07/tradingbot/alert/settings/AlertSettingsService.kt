package com.zor07.tradingbot.alert.settings

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zor07.tradingbot.alert.model.AlertType
import com.zor07.tradingbot.config.properties.PriceAlertProperties
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AlertSettingsService(
    private val repository: AlertSettingsRepository,
    private val objectMapper: ObjectMapper,
    private val properties: PriceAlertProperties
) {

    fun getPriceSettings(): PriceAlertSettings {
        val entity = repository.findByAlertType(AlertType.PRICE_MOVE.name)
            ?: return PriceAlertSettings(
                threshold = properties.threshold,
                candleInterval = properties.candleInterval,
                candleLimit = properties.candleLimit,
                cooldownMinutes = properties.cooldown.toMinutes().toInt()
            )
        return objectMapper.readValue(entity.settings)
    }

    fun savePriceSettings(settings: PriceAlertSettings) {
        val json = objectMapper.writeValueAsString(settings)
        val existing = repository.findByAlertType(AlertType.PRICE_MOVE.name)
        if (existing != null) {
            repository.save(existing.copy(settings = json, updatedAt = Instant.now()))
        } else {
            repository.save(AlertSettings(alertType = AlertType.PRICE_MOVE.name, settings = json))
        }
    }
}
