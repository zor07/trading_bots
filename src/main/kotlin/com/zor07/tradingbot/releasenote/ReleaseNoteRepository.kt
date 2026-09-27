package com.zor07.tradingbot.releasenote

import org.springframework.data.jpa.repository.JpaRepository

interface ReleaseNoteRepository : JpaRepository<ReleaseNote, Long> {
    fun findAllBySentFalse(): List<ReleaseNote>
}
