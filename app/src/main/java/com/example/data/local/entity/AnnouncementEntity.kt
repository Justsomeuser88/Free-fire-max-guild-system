package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "GENERAL", // DOG_TAG_RUSH, SCRIM_ALERT, TOURNAMENT, RECRUITMENT, REWARD, GENERAL
    val authorRole: String = "LEADER",
    val authorIgn: String = "亗 IGNITE・Viper 亗",
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
