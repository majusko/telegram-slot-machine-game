package com.soribot.slot.machine.telegram.bot.repository


import com.soribot.slot.machine.telegram.bot.service.LeaderboardService
import com.soribot.slot.machine.telegram.bot.service.SlotMachineService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("User")
data class Profile(
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var numberOfJackpots: Int = 0,
    var threeBars: Int = 0,
    var threeCherries: Int = 0,
    var threeLemons: Int = 0,
    var diceWins: Int = 0,
    var spentPoints: Long = 0,
    @Id
    var id: Int = 0
)

@ExperimentalCoroutinesApi
fun Profile.points() = ((numberOfJackpots * LeaderboardService.pointsForRecord) +
    ((threeLemons + threeBars + threeCherries) * LeaderboardService.pointsForOther) +
    diceWins * LeaderboardService.pointsForDice) +
    LeaderboardService.defaultBonus - spentPoints.fromMarioshi()

@ExperimentalCoroutinesApi
fun Long.fromMarioshi() = this.toDouble() / SlotMachineService.marioshiNumber.toDouble()