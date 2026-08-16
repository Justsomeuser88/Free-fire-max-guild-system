package com.example.data.repository

import com.example.data.local.dao.GuildDao
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class GuildRepository(private val guildDao: GuildDao) {

    val guildProfile: Flow<GuildProfileEntity?> = guildDao.getGuildProfileFlow()
    val allMembers: Flow<List<GuildMemberEntity>> = guildDao.getAllMembersFlow()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = guildDao.getAllAnnouncementsFlow()
    val allScrims: Flow<List<ScrimMatchEntity>> = guildDao.getAllScrimMatchesFlow()
    val allApplications: Flow<List<RecruitmentEntity>> = guildDao.getAllApplicationsFlow()
    val allRewards: Flow<List<GuildRewardEntity>> = guildDao.getAllRewardsFlow()

    suspend fun getGuildProfileDirect(): GuildProfileEntity? {
        return guildDao.getGuildProfileDirect()
    }

    suspend fun updateGuildProfile(profile: GuildProfileEntity) {
        guildDao.insertOrUpdateProfile(profile)
    }

    suspend fun insertMember(member: GuildMemberEntity): Long {
        return guildDao.insertMember(member)
    }

    suspend fun updateMember(member: GuildMemberEntity) {
        guildDao.updateMember(member)
    }

    suspend fun deleteMember(member: GuildMemberEntity) {
        guildDao.deleteMember(member)
    }

    suspend fun addDogTagsToMember(memberId: Long, addedTags: Int) {
        guildDao.addDogTagsToMember(memberId, addedTags)
        // Also update guild profile friday dog tag count
        val currentProfile = guildDao.getGuildProfileDirect() ?: GuildProfileEntity()
        val newTotal = currentProfile.fridayDogTagsTotal + addedTags
        guildDao.insertOrUpdateProfile(
            currentProfile.copy(
                fridayDogTagsTotal = newTotal,
                currentGlory = currentProfile.currentGlory + addedTags
            )
        )
    }

    suspend fun resetDailyDogTags() {
        guildDao.resetDailyDogTags()
    }

    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long {
        return guildDao.insertAnnouncement(announcement)
    }

    suspend fun deleteAnnouncement(announcement: AnnouncementEntity) {
        guildDao.deleteAnnouncement(announcement)
    }

    suspend fun updateAnnouncementPin(id: Long, isPinned: Boolean) {
        guildDao.updateAnnouncementPin(id, isPinned)
    }

    suspend fun insertScrim(scrim: ScrimMatchEntity): Long {
        return guildDao.insertScrim(scrim)
    }

    suspend fun updateScrim(scrim: ScrimMatchEntity) {
        guildDao.updateScrim(scrim)
    }

    suspend fun deleteScrim(scrim: ScrimMatchEntity) {
        guildDao.deleteScrim(scrim)
    }

    suspend fun insertApplication(application: RecruitmentEntity): Long {
        return guildDao.insertApplication(application)
    }

    suspend fun updateApplicationStatus(id: Long, status: String) {
        guildDao.updateApplicationStatus(id, status)
    }

    suspend fun deleteApplication(application: RecruitmentEntity) {
        guildDao.deleteApplication(application)
    }

    suspend fun insertReward(reward: GuildRewardEntity): Long {
        return guildDao.insertReward(reward)
    }

    suspend fun deleteReward(reward: GuildRewardEntity) {
        guildDao.deleteReward(reward)
    }
}
