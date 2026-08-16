package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guild_rewards")
data class GuildRewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val rewardType: String = "DIAMOND_AIRDROP",
    val recipientIgn: String,
    val reason: String,
    val dateAwarded: String,
    val isClaimed: Boolean = true
)
