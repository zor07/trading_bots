package com.zor07.tradingbot.web

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.alert.settings.PriceAlertSettings
import com.zor07.tradingbot.exchange.SymbolService
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
    private val symbolService: SymbolService
) {

    @GetMapping
    fun settingsPage(session: HttpSession, model: Model): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"
        val priceSettings = settingsService.getPriceSettings()
        val allSymbols = symbolService.getSymbols()

        model.addAttribute("priceSettings", priceSettings)
        model.addAttribute("excludedSymbols", HashSet(priceSettings.excludedSymbols))
        model.addAttribute("allSymbols", allSymbols)
        model.addAttribute("candleIntervals", listOf("1m", "5m", "15m", "1h", "4h"))
        return "settings"
    }

    @PostMapping
    fun saveSettings(
        session: HttpSession,
        @RequestParam enabled: Boolean = false,
        @RequestParam threshold: Double,
        @RequestParam candleInterval: String,
        @RequestParam candleLimit: Int,
        @RequestParam(required = false) excludedSymbols: List<String>?
    ): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"
        val settings = PriceAlertSettings(
            enabled = enabled,
            threshold = threshold,
            candleInterval = candleInterval,
            candleLimit = candleLimit,
            excludedSymbols = excludedSymbols ?: emptyList()
        )
        settingsService.savePriceSettings(settings)
        return "redirect:/settings"
    }
}
