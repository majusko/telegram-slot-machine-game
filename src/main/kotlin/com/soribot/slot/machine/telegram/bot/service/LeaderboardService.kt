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
        const val recordsLine = "%s - %s rekordów (%s cytrynowy, %s bar, %s wiśnia)"
        const val recordsText = "Rekordy: \n"
        const val leaderboardText = "Tablica: \n"
        const val leaderboardLine = "%s - %s punktów"
        const val pointsForRecord = 10
        const val pointsForOther = 1
    }

    fun start(update: Update) {
        if (update.message.hasText() && leaderBoardTrigger.find { update.message.text.contains(it) } != null) {
            sendLeaderboards(update.message)
        }
    }

    fun sendLeaderboards(message: Message) {
        val allUsers = profileService.findAll()
        val pointsLeaderBoardText = allUsers
            .map { it to (it.numberOfJackpots * pointsForRecord) + ((it.threeLemons + it.threeBars + it.threeCherries) * pointsForOther) }
            .sortedByDescending { it.second }
            .joinToString(separator = "\n") {
                leaderboardLine.format(it.first.firstName + " " + it.first.lastName, it.second)
            }.let { leaderboardText + it }
        val recordsLeaderBoardText = allUsers.sortedByDescending { it.numberOfJackpots }
            .joinToString(separator = "\n") {
                recordsLine.format(it.firstName + " " + it.lastName, it.numberOfJackpots, it.threeLemons, it.threeBars, it.threeCherries)
            }
            .let { recordsText + it }

        botSender.textAsync(message.chatId, "$pointsLeaderBoardText\n\n$recordsLeaderBoardText")
    }
}