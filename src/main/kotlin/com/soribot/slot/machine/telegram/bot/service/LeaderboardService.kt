package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
@ExperimentalCoroutinesApi
class LeaderboardService(
    private val profileService: ProfileService,
    private val botSender: BotSender
) {
    companion object {
        val leaderBoardTrigger = listOf("leaderboard", "tabulka", "liderów", "tablica")
        const val leaderboardLine = "%s - %s rekordów "
        const val leaderboardText = "Tablica: \n"
    }

    fun start(update: Update) {
        if (update.message.hasText() && leaderBoardTrigger.find { update.message.text.contains(it) } != null) {
            sendLeaderboards(update.message)
        }
    }

    fun sendLeaderboards(message: Message) = profileService.findAll()
        .sortedByDescending { it.numberOfJackpots }
        .joinToString(separator = "\n") {
            leaderboardLine.format(it.firstName + "" + it.lastName, it.numberOfJackpots)
        }
        .also { botSender.textAsync(message.chatId, leaderboardText + it) }
}