package com.zor07.tradingbot.web

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.alert.settings.LiquidationAlertSettings
import com.zor07.tradingbot.alert.settings.OiAlertSettings
import com.zor07.tradingbot.alert.settings.PriceAlertSettings
import com.zor07.tradingbot.config.properties.TelegramProperties
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings")
class SettingsController(
    private val settingsService: AlertSettingsService,
    private val telegramProperties: TelegramProperties
) {

    @GetMapping
    fun settingsPage(session: HttpSession, model: Model): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"
        val priceSettings = settingsService.getPriceSettings()
        val oiSettings = settingsService.getOiSettings()

        model.addAttribute("priceSettings", priceSettings)
        model.addAttribute("candleIntervals", listOf("1m", "3m", "5m", "15m", "1h", "4h"))
        model.addAttribute("oiSettings", oiSettings)
        model.addAttribute("oiPeriods", listOf("5m", "15m", "30m", "1h", "2h", "4h", "6h", "12h", "1d"))
        model.addAttribute("liquidationSettings", settingsService.getLiquidationSettings())
        model.addAttribute("priceBotUrl", "https://t.me/${telegramProperties.priceBot.username}")
        model.addAttribute("oiBotUrl", "https://t.me/${telegramProperties.oiBot.username}")
        model.addAttribute("liquidationBotUrl", "https://t.me/${telegramProperties.liquidationBot.username}")
        model.addAttribute("watchlistCount", settingsService.getWatchlist().size)
        return "settings"
    }

    @PostMapping
    fun saveSettings(
        session: HttpSession,
        @RequestParam enabled: Boolean = false,
        @RequestParam threshold: Double,
        @RequestParam candleInterval: String,
        @RequestParam candleLimit: Int,
        @RequestParam oiEnabled: Boolean = false,
        @RequestParam oiThreshold: Double,
        @RequestParam oiPeriod: String,
        @RequestParam liquidationEnabled: Boolean = false,
        @RequestParam liquidationMinUsdValue: Double
    ): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"

        settingsService.savePriceSettings(PriceAlertSettings(
            enabled = enabled,
            threshold = threshold,
            candleInterval = candleInterval,
            candleLimit = candleLimit
        ))

        settingsService.saveOiSettings(OiAlertSettings(
            enabled = oiEnabled,
            threshold = oiThreshold,
            period = oiPeriod
        ))

        settingsService.saveLiquidationSettings(LiquidationAlertSettings(
            enabled = liquidationEnabled,
            minUsdValue = liquidationMinUsdValue
        ))

        return "redirect:/settings"
    }
}
