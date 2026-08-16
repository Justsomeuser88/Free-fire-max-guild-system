package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recruitment_applications")
data class RecruitmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val applicantIgn: String,
    val applicantUid: String,
    val rank: String = "HEROIC",
    val level: Int = 58,
    val kdRatio: Double = 3.1,
    val headshotRate: Int = 45,
    val discordTag: String = "",
    val preferredRole: String = "RUSHER",
    val reason: String = "Looking for a dedicated esports tournament guild to grind scrims daily.",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val appliedTime: Long = System.currentTimeMillis()
)
