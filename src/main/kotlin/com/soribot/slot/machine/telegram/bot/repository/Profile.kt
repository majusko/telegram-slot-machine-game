package com.soribot.slot.machine.telegram.bot.repository


import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.util.*

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
    @Id
    var id: Int = 0
)