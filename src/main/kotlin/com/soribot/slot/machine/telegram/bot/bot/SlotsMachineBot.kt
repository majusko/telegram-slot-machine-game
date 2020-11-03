package com.soribot.slot.machine.telegram.bot.bot

import com.soribot.slot.machine.telegram.bot.utils.subscribe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.net.URLEncoder

@Component
@ExperimentalCoroutinesApi
class SlotsMachineBot(
    val botSentMessages: BroadcastChannel<Message>,
    val slotMachineReceiver: BroadcastChannel<Update>,
    slotMachineSender: BroadcastChannel<BotApiMethod<Message>>
) : TelegramLongPollingBot() {

    @Value("\${telegram.bot.username}")
    var username: String = ""

    @Value("\${telegram.bot.token}")
    var token: String = ""

    init {
        slotMachineSender.subscribe { botSentMessages.send(execute(it)) }
    }

    override fun getBotToken(): String = URLEncoder.encode(token, "utf-8")

    override fun getBotUsername() = username

    override fun onUpdateReceived(update: Update) = runBlocking {
        if (update.hasMessage() && (update.message.hasText() || update.message.hasDice())) {
            launch { slotMachineReceiver.send(update) }
        }
    }
}