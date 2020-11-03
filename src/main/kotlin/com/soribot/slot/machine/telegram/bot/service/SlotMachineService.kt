package com.soribot.slot.machine.telegram.bot.service

import com.soribot.slot.machine.telegram.bot.bot.BotSender
import com.soribot.slot.machine.telegram.bot.repository.Profile
import com.soribot.slot.machine.telegram.bot.utils.subscribe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
@ExperimentalCoroutinesApi
class SlotMachineService(
    private val profileService: ProfileService,
    private val botSender: BotSender,
    private val leaderboardService: LeaderboardService,
    botSentMessages: BroadcastChannel<Message>
) {
    companion object {
        const val diceEmoji = "\uD83C\uDFB2"
        const val slotEmoji = "\uD83C\uDFB0"
        const val congratsMessage = "Gratulacje księżniczko %s"
        const val jackPotValueId = 64
        const val lemonValueId = 43
        const val cherryValueId = 22
        const val barValueId = 1
    }

    init {
        botSentMessages.subscribe {
            if (it.dice != null && it.dice.emoji == diceEmoji && it.isReply) {
                diceProcessing(it)
            }
        }
    }

    fun start(update: Update) {
        if (update.message.hasText() && !update.message.isReply && update.message.text.toIntOrNull() != null) {
            botSender.diceAsync(update.message.chatId, update.message.messageId)
        }

        if (update.message.dice != null && update.message.dice.emoji == slotEmoji) {
            slotsProcess(update.message)
        }
    }

    private fun diceProcessing(message: Message) {
        if (message.replyToMessage.text.toIntOrNull() == message.dice.value) {
            dices(message.replyToMessage)
        }
    }

    private fun slotsProcess(message: Message) = message.dice.also {
        when (it.value) {
            jackPotValueId -> jackpot(message)
            lemonValueId -> lemon(message)
            cherryValueId -> cherry(message)
            barValueId -> bars(message)
            else -> {
            }
        }
    }

    private fun jackpot(message: Message) = profileService.findByIdOrRegister(message)
        .apply { numberOfJackpots += 1 }
        .also { afterWin(it, message) }

    private fun lemon(message: Message) = profileService.findByIdOrRegister(message)
        .apply { threeLemons += 1 }
        .also { afterWin(it, message) }

    private fun cherry(message: Message) = profileService.findByIdOrRegister(message)
        .apply { threeCherries += 1 }
        .also { afterWin(it, message) }

    private fun bars(message: Message) = profileService.findByIdOrRegister(message)
        .apply { threeBars += 1 }
        .also { afterWin(it, message) }

    private fun dices(message: Message) = profileService.findByIdOrRegister(message)
        .apply { diceWins += 1 }
        .also { afterWin(it, message) }

    private fun afterWin(profile: Profile, message: Message) {
        profileService.save(profile)
        botSender.textAsync(message.chatId, congratsMessage.format(profile.firstName + " " + profile.lastName))
        leaderboardService.sendLightLeaderboards(message)
    }
}