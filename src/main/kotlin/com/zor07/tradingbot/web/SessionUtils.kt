package com.zor07.tradingbot.web

import jakarta.servlet.http.HttpSession

object SessionUtils {
    private const val USER_ID_KEY = "userId"

    fun getUserId(session: HttpSession): Long? = session.getAttribute(USER_ID_KEY) as? Long
    fun setUserId(session: HttpSession, userId: Long) = session.setAttribute(USER_ID_KEY, userId)
}
