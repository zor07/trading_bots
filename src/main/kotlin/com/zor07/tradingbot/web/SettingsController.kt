package com.zor07.tradingbot.web

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.alert.settings.LongShortRatioSettings
import com.zor07.tradingbot.alert.settings.PriceAlertSettings
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
    private val settingsService: AlertSettingsService
) {

    @GetMapping
    fun settingsPage(session: HttpSession, model: Model): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"
        val priceSettings = settingsService.getPriceSettings()
        val lsrSettings = settingsService.getLsrSettings()

        model.addAttribute("priceSettings", priceSettings)
        model.addAttribute("candleIntervals", listOf("1m", "5m", "15m", "1h", "4h"))
        model.addAttribute("lsrSettings", lsrSettings)
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
        @RequestParam lsrEnabled: Boolean = false,
        @RequestParam lsrAccountThreshold: Double,
        @RequestParam lsrPositionThreshold: Double
    ): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"

        settingsService.savePriceSettings(PriceAlertSettings(
            enabled = enabled,
            threshold = threshold,
            candleInterval = candleInterval,
            candleLimit = candleLimit
        ))

        settingsService.saveLsrSettings(LongShortRatioSettings(
            enabled = lsrEnabled,
            accountThreshold = lsrAccountThreshold,
            positionThreshold = lsrPositionThreshold
        ))

        return "redirect:/settings"
    }
}
