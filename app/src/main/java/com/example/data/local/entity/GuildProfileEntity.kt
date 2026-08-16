package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guild_profile")
data class GuildProfileEntity(
    @PrimaryKey val id: Int = 1,
    val guildName: String = "🔥 IGNITE ESPORTS 🔥",
    val guildTag: String = "IGNT",
    val guildUid: String = "682940184",
    val guildLevel: Int = 6,
    val currentGlory: Int = 184500,
    val targetGlory: Int = 250000,
    val region: String = "India / South Asia (IND)",
    val slogan: String = "No Mercy. Only Booyah! 🏆 Free Fire Champions",
    val leaderIgn: String = "亗 IGNITE・Viper 亗",
    val leaderUid: String = "284910294",
    val fridayDogTagsTotal: Int = 1420,
    val fridayDogTagsTarget: Int = 1800,
    val roomCardsClaimed: Boolean = false,
    val minRankRequirement: String = "HEROIC",
    val minKdRequirement: Double = 2.8,
    val minLevelRequirement: Int = 55,
    val discordLink: String = "discord.gg/ignite-ff",
    val whatsappGroup: String = "chat.whatsapp.com/IGNITEffGuild"
)
