package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GuildDao {

    // Guild Profile
    @Query("SELECT * FROM guild_profile WHERE id = 1 LIMIT 1")
    fun getGuildProfileFlow(): Flow<GuildProfileEntity?>

    @Query("SELECT * FROM guild_profile WHERE id = 1 LIMIT 1")
    suspend fun getGuildProfileDirect(): GuildProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: GuildProfileEntity)

    // Members
    @Query("SELECT * FROM guild_members ORDER BY level DESC, totalGloryEarned DESC")
    fun getAllMembersFlow(): Flow<List<GuildMemberEntity>>

    @Query("SELECT * FROM guild_members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: Long): GuildMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: GuildMemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMembers(members: List<GuildMemberEntity>)

    @Update
    suspend fun updateMember(member: GuildMemberEntity)

    @Delete
    suspend fun deleteMember(member: GuildMemberEntity)

    @Query("UPDATE guild_members SET todayDogTags = todayDogTags + :addedTags, weeklyDogTags = weeklyDogTags + :addedTags, totalGloryEarned = totalGloryEarned + :addedTags WHERE id = :memberId")
    suspend fun addDogTagsToMember(memberId: Long, addedTags: Int)

    @Query("UPDATE guild_members SET todayDogTags = 0")
    suspend fun resetDailyDogTags()

    @Query("SELECT COUNT(*) FROM guild_members")
    suspend fun getMemberCount(): Int

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY isPinned DESC, timestamp DESC")
    fun getAllAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnnouncements(announcements: List<AnnouncementEntity>)

    @Delete
    suspend fun deleteAnnouncement(announcement: AnnouncementEntity)

    @Query("UPDATE announcements SET isPinned = :isPinned WHERE id = :id")
    suspend fun updateAnnouncementPin(id: Long, isPinned: Boolean)

    // Scrim Matches
    @Query("SELECT * FROM scrim_matches ORDER BY id DESC")
    fun getAllScrimMatchesFlow(): Flow<List<ScrimMatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrim(scrim: ScrimMatchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllScrims(scrims: List<ScrimMatchEntity>)

    @Update
    suspend fun updateScrim(scrim: ScrimMatchEntity)

    @Delete
    suspend fun deleteScrim(scrim: ScrimMatchEntity)

    // Recruitment Applications
    @Query("SELECT * FROM recruitment_applications ORDER BY appliedTime DESC")
    fun getAllApplicationsFlow(): Flow<List<RecruitmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: RecruitmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllApplications(applications: List<RecruitmentEntity>)

    @Query("UPDATE recruitment_applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: Long, status: String)

    @Delete
    suspend fun deleteApplication(application: RecruitmentEntity)

    // Rewards
    @Query("SELECT * FROM guild_rewards ORDER BY id DESC")
    fun getAllRewardsFlow(): Flow<List<GuildRewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: GuildRewardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRewards(rewards: List<GuildRewardEntity>)

    @Delete
    suspend fun deleteReward(reward: GuildRewardEntity)
}
