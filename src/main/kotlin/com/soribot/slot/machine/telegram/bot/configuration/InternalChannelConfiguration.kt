package com.soribot.slot.machine.telegram.bot.configuration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@ExperimentalCoroutinesApi
@Configuration
class InternalChannelConfiguration {

    @Bean
    fun slotMachineReceiver() = BroadcastChannel<Update>(10000)

    @Bean
    fun botSentMessages() = BroadcastChannel<Message>(10000)

    @Bean
    fun slotMachineSender() = BroadcastChannel<BotApiMethod<Message>>(10000)
}