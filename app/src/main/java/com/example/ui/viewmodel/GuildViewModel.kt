package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GuildDatabase
import com.example.data.local.entity.*
import com.example.data.repository.GuildRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GuildViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuildRepository

    val guildProfile: StateFlow<GuildProfileEntity>
    val allMembers: StateFlow<List<GuildMemberEntity>>
    val allAnnouncements: StateFlow<List<AnnouncementEntity>>
    val allScrims: StateFlow<List<ScrimMatchEntity>>
    val allApplications: StateFlow<List<RecruitmentEntity>>
    val allRewards: StateFlow<List<GuildRewardEntity>>

    // Roster Filtering State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRoleFilter = MutableStateFlow("ALL")
    val selectedRoleFilter: StateFlow<String> = _selectedRoleFilter.asStateFlow()

    private val _selectedRankFilter = MutableStateFlow("ALL")
    val selectedRankFilter: StateFlow<String> = _selectedRankFilter.asStateFlow()

    private val _selectedSquadFilter = MutableStateFlow("ALL")
    val selectedSquadFilter: StateFlow<String> = _selectedSquadFilter.asStateFlow()

    // Scrims Filter
    private val _selectedScrimFilter = MutableStateFlow("ALL") // ALL, UPCOMING, LIVE, COMPLETED
    val selectedScrimFilter: StateFlow<String> = _selectedScrimFilter.asStateFlow()

    // Filtered Members Flow
    val filteredMembers: StateFlow<List<GuildMemberEntity>>

    init {
        val database = GuildDatabase.getDatabase(application, viewModelScope)
        repository = GuildRepository(database.guildDao())

        guildProfile = repository.guildProfile
            .map { it ?: GuildProfileEntity() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                GuildProfileEntity()
            )

        allMembers = repository.allMembers
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

        allAnnouncements = repository.allAnnouncements
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

        allScrims = repository.allScrims
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

        allApplications = repository.allApplications
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

        allRewards = repository.allRewards
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

        filteredMembers = combine(
            allMembers,
            _searchQuery,
            _selectedRoleFilter,
            _selectedRankFilter,
            _selectedSquadFilter
        ) { members, query, role, rank, squad ->
            members.filter { member ->
                val matchesQuery = query.isEmpty() ||
                        member.ign.contains(query, ignoreCase = true) ||
                        member.gameUid.contains(query, ignoreCase = true)

                val matchesRole = role == "ALL" || member.role.equals(role, ignoreCase = true)
                val matchesRank = rank == "ALL" || member.rank.equals(rank, ignoreCase = true)
                val matchesSquad = squad == "ALL" || member.squadGroup.equals(squad, ignoreCase = true)

                matchesQuery && matchesRole && matchesRank && matchesSquad
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    // Filter setters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setRoleFilter(role: String) {
        _selectedRoleFilter.value = role
    }

    fun setRankFilter(rank: String) {
        _selectedRankFilter.value = rank
    }

    fun setSquadFilter(squad: String) {
        _selectedSquadFilter.value = squad
    }

    fun setScrimFilter(filter: String) {
        _selectedScrimFilter.value = filter
    }

    // Profile actions
    fun updateGuildProfile(profile: GuildProfileEntity) {
        viewModelScope.launch {
            repository.updateGuildProfile(profile)
        }
    }

    fun claimRoomCards() {
        viewModelScope.launch {
            val current = guildProfile.value
            repository.updateGuildProfile(current.copy(roomCardsClaimed = true))
        }
    }

    // Member actions
    fun addMember(member: GuildMemberEntity) {
        viewModelScope.launch {
            repository.insertMember(member)
        }
    }

    fun updateMember(member: GuildMemberEntity) {
        viewModelScope.launch {
            repository.updateMember(member)
        }
    }

    fun deleteMember(member: GuildMemberEntity) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }

    fun addDogTagsToMember(memberId: Long, tags: Int) {
        viewModelScope.launch {
            repository.addDogTagsToMember(memberId, tags)
        }
    }

    fun updateMemberRole(member: GuildMemberEntity, newRole: String) {
        viewModelScope.launch {
            repository.updateMember(member.copy(role = newRole))
        }
    }

    fun updateMemberSquad(member: GuildMemberEntity, newSquad: String) {
        viewModelScope.launch {
            repository.updateMember(member.copy(squadGroup = newSquad))
        }
    }

    fun addStrikeToMember(member: GuildMemberEntity) {
        viewModelScope.launch {
            repository.updateMember(member.copy(strikesCount = member.strikesCount + 1))
        }
    }

    // Announcement actions
    fun addAnnouncement(title: String, content: String, category: String, authorRole: String, authorIgn: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.insertAnnouncement(
                AnnouncementEntity(
                    title = title,
                    content = content,
                    category = category,
                    authorRole = authorRole,
                    authorIgn = authorIgn,
                    isPinned = isPinned
                )
            )
        }
    }

    fun deleteAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }

    fun toggleAnnouncementPin(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.updateAnnouncementPin(announcement.id, !announcement.isPinned)
        }
    }

    // Scrim actions
    fun addScrim(scrim: ScrimMatchEntity) {
        viewModelScope.launch {
            repository.insertScrim(scrim)
        }
    }

    fun updateScrim(scrim: ScrimMatchEntity) {
        viewModelScope.launch {
            repository.updateScrim(scrim)
        }
    }

    fun deleteScrim(scrim: ScrimMatchEntity) {
        viewModelScope.launch {
            repository.deleteScrim(scrim)
        }
    }

    // Recruitment actions
    fun addApplication(application: RecruitmentEntity) {
        viewModelScope.launch {
            repository.insertApplication(application)
        }
    }

    fun approveApplication(application: RecruitmentEntity) {
        viewModelScope.launch {
            repository.updateApplicationStatus(application.id, "APPROVED")
            // Automatically add them to the guild roster as RECRUIT!
            repository.insertMember(
                GuildMemberEntity(
                    ign = application.applicantIgn,
                    gameUid = application.applicantUid,
                    role = "RECRUIT",
                    rank = application.rank,
                    level = application.level,
                    kdRatio = application.kdRatio,
                    headshotPercentage = application.headshotRate,
                    combatRole = application.preferredRole,
                    squadGroup = "ROOKIE_SQUAD",
                    activeStatus = "ONLINE",
                    notes = "Approved from application: ${application.reason}"
                )
            )
        }
    }

    fun rejectApplication(application: RecruitmentEntity) {
        viewModelScope.launch {
            repository.updateApplicationStatus(application.id, "REJECTED")
        }
    }

    fun deleteApplication(application: RecruitmentEntity) {
        viewModelScope.launch {
            repository.deleteApplication(application)
        }
    }

    // Rewards actions
    fun addReward(reward: GuildRewardEntity) {
        viewModelScope.launch {
            repository.insertReward(reward)
        }
    }

    fun deleteReward(reward: GuildRewardEntity) {
        viewModelScope.launch {
            repository.deleteReward(reward)
        }
    }
}
