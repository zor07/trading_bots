package com.zor07.tradingbot.web

import com.zor07.tradingbot.alert.settings.AlertSettingsService
import com.zor07.tradingbot.exchange.SymbolCacheService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/settings/symbols")
class SymbolsController(
    private val symbolCacheService: SymbolCacheService,
    private val settingsService: AlertSettingsService
) {

    @GetMapping
    fun symbolsPage(session: HttpSession, model: Model): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"
        val watchlist = settingsService.getWatchlist()
        model.addAttribute("allSymbols", symbolCacheService.getAll())
        model.addAttribute("watchlist", HashSet(watchlist))
        model.addAttribute("watchlistCount", watchlist.size)
        return "symbols"
    }

    @PostMapping
    fun saveSymbols(
        session: HttpSession,
        @RequestParam(required = false) symbols: List<String>?
    ): String {
        SessionUtils.getUserId(session) ?: return "redirect:/login"
        val selected = (symbols ?: emptyList()).take(50)
        settingsService.saveWatchlist(selected)
        return "redirect:/settings/symbols"
    }
}
