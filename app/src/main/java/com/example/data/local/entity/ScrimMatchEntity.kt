package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scrim_matches")
data class ScrimMatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val opponentGuild: String,
    val mode: String = "BATTLE_ROYALE_SQUAD", // BATTLE_ROYALE_SQUAD, CLASH_SQUAD, LONE_WOLF
    val map: String = "BERMUDA", // BERMUDA, PURGATORY, KALAHARI, ALPINE, NEXTERRA
    val scheduledTime: String,
    val roomId: String = "",
    val roomPassword: String = "",
    val squadAssigned: String = "SQUAD_ALPHA",
    val status: String = "UPCOMING", // UPCOMING, LIVE, COMPLETED, CANCELLED
    val result: String = "PENDING", // WIN, LOSS, DRAW, PENDING
    val ourScore: Int = 0,
    val opponentScore: Int = 0,
    val totalKills: Int = 0,
    val placementPoints: Int = 0,
    val mvpIgn: String = "",
    val customRules: String = "Standard FFCS: Gun Property OFF, Skills ON, Grenade limit 1/round, No Roof Camping",
    val proofNotes: String = ""
)
