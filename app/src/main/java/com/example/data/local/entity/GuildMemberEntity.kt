package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guild_members")
data class GuildMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ign: String,
    val gameUid: String,
    val role: String = "MEMBER", // LEADER, CO_LEADER, OFFICER, ELITE, MEMBER, RECRUIT
    val rank: String = "HEROIC", // GRANDMASTER, MASTER, HEROIC, DIAMOND, etc.
    val level: Int = 60,
    val starsOrPoints: Int = 45,
    val todayDogTags: Int = 0,
    val weeklyDogTags: Int = 80,
    val totalGloryEarned: Int = 4200,
    val kdRatio: Double = 3.2,
    val headshotPercentage: Int = 48,
    val winRatePercentage: Int = 34,
    val matchesPlayed: Int = 450,
    val squadGroup: String = "UNASSIGNED", // SQUAD_ALPHA, SQUAD_BRAVO, CS_DOMINATORS, ROOKIE_SQUAD, UNASSIGNED
    val combatRole: String = "RUSHER", // IGL, RUSHER, SNIPER, FLANKER, GRENADIER, SUPPORT
    val activeStatus: String = "ONLINE", // ONLINE, IN_GAME, RECENTLY, INACTIVE
    val favoriteGunCombo: String = "Woodpecker + MP40",
    val characterSkillActive: String = "Tatsuya",
    val characterSkillPassives: String = "Hayato + Kelly + Moco",
    val strikesCount: Int = 0,
    val joinDate: String = "2024-01-15",
    val notes: String = ""
)
