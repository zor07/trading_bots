package com.zor07.tradingbot.user.alert

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zor07.tradingbot.alert.model.AlertType
import com.zor07.tradingbot.user.alert.settings.PriceAlertUserSettings
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserAlertSettingsService(
    private val repository: UserAlertSettingsRepository,
    private val objectMapper: ObjectMapper
) {

    fun getPriceSettings(): PriceAlertUserSettings {
        val entity = repository.findByAlertType(AlertType.PRICE_MOVE.name)
            ?: return PriceAlertUserSettings()
        return objectMapper.readValue(entity.settings)
    }

    fun savePriceSettings(settings: PriceAlertUserSettings) {
        val json = objectMapper.writeValueAsString(settings)
        val existing = repository.findByAlertType(AlertType.PRICE_MOVE.name)
        if (existing != null) {
            repository.save(existing.copy(settings = json, updatedAt = Instant.now()))
        } else {
            repository.save(UserAlertSettings(alertType = AlertType.PRICE_MOVE.name, settings = json))
        }
    }
}
