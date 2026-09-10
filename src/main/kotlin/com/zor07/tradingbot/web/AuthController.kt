package com.zor07.tradingbot.web

import com.zor07.tradingbot.user.UserService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class AuthController(private val userService: UserService) {

    @GetMapping("/login")
    fun loginPage(
        @RequestParam(required = false) token: String?,
        model: Model
    ): String {
        if (token != null) model.addAttribute("token", token)
        return "login"
    }

    @PostMapping("/login")
    fun login(
        @RequestParam token: String,
        session: HttpSession,
        model: Model
    ): String {
        val user = userService.findByToken(token)
        if (user == null) {
            model.addAttribute("token", token)
            model.addAttribute("error", "Неверный токен")
            return "login"
        }
        SessionUtils.setUserId(session, user.id)
        return "redirect:/settings"
    }
}
