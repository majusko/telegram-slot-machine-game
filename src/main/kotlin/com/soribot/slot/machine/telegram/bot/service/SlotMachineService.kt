package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
@ExperimentalCoroutinesApi
class SlotMachineService(
    private val profileService: ProfileService,
    private val botSender: BotSender,
    private val leaderboardService: LeaderboardService
) {
    companion object {
        const val slotEmoji = "\uD83C\uDFB0"
        const val congratsMessage = "Gratulacje księżniczko %s"
        const val jackPotValueId = 64
    }

    fun start(update: Update) {
        if (update.message.dice != null && update.message.dice.emoji == slotEmoji) {
            diceProcessing(update.message)
        }
    }

    private fun diceProcessing(message: Message) = message.dice.also {
        when (it.value) {
            jackPotValueId -> jackpot(message)
            else -> {
            }
        }
    }

    private fun jackpot(message: Message) {
        profileService.findByIdOrRegister(message).apply {
            numberOfJackpots += 1
        }.also {
            profileService.save(it)
            botSender.textAsync(message.chatId, congratsMessage.format(it.firstName + " " + it.lastName))
            leaderboardService.sendLeaderboards(message)
        }
    }
}