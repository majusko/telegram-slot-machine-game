package com.soribot.slot.machine.telegram.bot.router

import com.soribot.slot.machine.telegram.bot.service.LeaderboardService
import com.soribot.slot.machine.telegram.bot.service.ProfileManagementService
import com.soribot.slot.machine.telegram.bot.service.RegistrationService
import com.soribot.slot.machine.telegram.bot.service.SlotMachineService
import com.soribot.slot.machine.telegram.bot.utils.subscribe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@ExperimentalCoroutinesApi
@Component
class MessagesRouter(
    slotMachineReceiver: BroadcastChannel<Update>,
    val registrationService: RegistrationService,
    val leaderboardService: LeaderboardService,
    val slotMachineService: SlotMachineService,
    val profileManagementService: ProfileManagementService
) {

    init {
        slotMachineReceiver.subscribe { route(it) }
    }

    suspend fun route(update: Update) {
        registrationService.start(update)
        slotMachineService.start(update)
        leaderboardService.start(update)
        profileManagementService.start(update)
    }
}