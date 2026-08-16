package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.GuildDao
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        GuildProfileEntity::class,
        GuildMemberEntity::class,
        AnnouncementEntity::class,
        ScrimMatchEntity::class,
        RecruitmentEntity::class,
        GuildRewardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GuildDatabase : RoomDatabase() {

    abstract fun guildDao(): GuildDao

    companion object {
        @Volatile
        private var INSTANCE: GuildDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): GuildDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GuildDatabase::class.java,
                    "free_fire_guild.db"
                )
                    .addCallback(GuildDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class GuildDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.guildDao())
                }
            }
        }

        suspend fun populateInitialData(dao: GuildDao) {
            // Guild Profile
            val profile = GuildProfileEntity(
                id = 1,
                guildName = "亗 IGNITE ESPORTS 亗",
                guildTag = "IGNT",
                guildUid = "682940184",
                guildLevel = 6,
                currentGlory = 184500,
                targetGlory = 250000,
                region = "India / South Asia (IND)",
                slogan = "No Mercy. Only Booyah! 🏆 Top Tier Free Fire Guild",
                leaderIgn = "亗 IGNITE・Viper 亗",
                leaderUid = "284910294",
                fridayDogTagsTotal = 1520,
                fridayDogTagsTarget = 1800,
                roomCardsClaimed = false,
                minRankRequirement = "HEROIC",
                minKdRequirement = 3.0,
                minLevelRequirement = 60,
                discordLink = "discord.gg/ignite-ff",
                whatsappGroup = "chat.whatsapp.com/IGNITEffGuild"
            )
            dao.insertOrUpdateProfile(profile)

            // Initial Roster
            val initialMembers = listOf(
                GuildMemberEntity(
                    ign = "亗 IGNITE・Viper 亗",
                    gameUid = "284910294",
                    role = "LEADER",
                    rank = "GRANDMASTER",
                    level = 78,
                    starsOrPoints = 142,
                    todayDogTags = 120,
                    weeklyDogTags = 260,
                    totalGloryEarned = 14800,
                    kdRatio = 4.8,
                    headshotPercentage = 68,
                    winRatePercentage = 42,
                    matchesPlayed = 1240,
                    squadGroup = "SQUAD_ALPHA",
                    combatRole = "IGL",
                    activeStatus = "ONLINE",
                    favoriteGunCombo = "Woodpecker + MP40",
                    characterSkillActive = "Tatsuya",
                    characterSkillPassives = "Kelly + Hayato + Moco",
                    joinDate = "2023-01-10",
                    notes = "Guild Leader & Head of Esports Division."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Shadow 𝄟",
                    gameUid = "592019482",
                    role = "CO_LEADER",
                    rank = "GRANDMASTER",
                    level = 75,
                    starsOrPoints = 98,
                    todayDogTags = 100,
                    weeklyDogTags = 220,
                    totalGloryEarned = 12400,
                    kdRatio = 4.5,
                    headshotPercentage = 72,
                    winRatePercentage = 39,
                    matchesPlayed = 980,
                    squadGroup = "SQUAD_ALPHA",
                    combatRole = "SNIPER",
                    activeStatus = "IN_GAME",
                    favoriteGunCombo = "AWM + Desert Eagle",
                    characterSkillActive = "Iris",
                    characterSkillPassives = "Rafael + Laura + Maro",
                    joinDate = "2023-02-14",
                    notes = "Primary AWM Sniper in tournaments."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Ragnar ⚡",
                    gameUid = "481029381",
                    role = "OFFICER",
                    rank = "MASTER",
                    level = 71,
                    starsOrPoints = 64,
                    todayDogTags = 90,
                    weeklyDogTags = 190,
                    totalGloryEarned = 9800,
                    kdRatio = 3.9,
                    headshotPercentage = 54,
                    winRatePercentage = 36,
                    matchesPlayed = 850,
                    squadGroup = "SQUAD_ALPHA",
                    combatRole = "RUSHER",
                    activeStatus = "ONLINE",
                    favoriteGunCombo = "M1887 + MP5-III",
                    characterSkillActive = "Alok",
                    characterSkillPassives = "Kelly + Caroline + Hayato",
                    joinDate = "2023-05-01",
                    notes = "Frontline Rusher & Scrim Organizer."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Phoenix 🔥",
                    gameUid = "772910384",
                    role = "OFFICER",
                    rank = "MASTER",
                    level = 69,
                    starsOrPoints = 58,
                    todayDogTags = 80,
                    weeklyDogTags = 170,
                    totalGloryEarned = 8900,
                    kdRatio = 3.7,
                    headshotPercentage = 50,
                    winRatePercentage = 35,
                    matchesPlayed = 790,
                    squadGroup = "SQUAD_ALPHA",
                    combatRole = "GRENADIER",
                    activeStatus = "RECENTLY",
                    favoriteGunCombo = "M79 + Groza",
                    characterSkillActive = "Steffie",
                    characterSkillPassives = "Alvaro + Kelly + Maxim",
                    joinDate = "2023-06-12",
                    notes = "Master Grenadier. Clutch Specialist."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Ghost 𓊈",
                    gameUid = "381920391",
                    role = "ELITE",
                    rank = "HEROIC",
                    level = 66,
                    starsOrPoints = 42,
                    todayDogTags = 110,
                    weeklyDogTags = 210,
                    totalGloryEarned = 7600,
                    kdRatio = 3.4,
                    headshotPercentage = 46,
                    winRatePercentage = 32,
                    matchesPlayed = 620,
                    squadGroup = "SQUAD_BRAVO",
                    combatRole = "FLANKER",
                    activeStatus = "ONLINE",
                    favoriteGunCombo = "AC80 + Thompson",
                    characterSkillActive = "K",
                    characterSkillPassives = "Miguel + Jota + Kelly",
                    joinDate = "2023-08-20",
                    notes = "Lineup Bravo IGL."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Apex ☬",
                    gameUid = "619283740",
                    role = "ELITE",
                    rank = "HEROIC",
                    level = 65,
                    starsOrPoints = 38,
                    todayDogTags = 70,
                    weeklyDogTags = 160,
                    totalGloryEarned = 6900,
                    kdRatio = 3.2,
                    headshotPercentage = 42,
                    winRatePercentage = 30,
                    matchesPlayed = 580,
                    squadGroup = "CS_DOMINATORS",
                    combatRole = "RUSHER",
                    activeStatus = "IN_GAME",
                    favoriteGunCombo = "M1014 + UMP",
                    characterSkillActive = "Chrono",
                    characterSkillPassives = "Hayato + Kelly + D-Bee",
                    joinDate = "2023-10-05",
                    notes = "Clash Squad 1v4 king."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Nova 亗",
                    gameUid = "192840192",
                    role = "MEMBER",
                    rank = "HEROIC",
                    level = 62,
                    starsOrPoints = 25,
                    todayDogTags = 60,
                    weeklyDogTags = 140,
                    totalGloryEarned = 5400,
                    kdRatio = 2.9,
                    headshotPercentage = 38,
                    winRatePercentage = 28,
                    matchesPlayed = 460,
                    squadGroup = "SQUAD_BRAVO",
                    combatRole = "SUPPORT",
                    activeStatus = "RECENTLY",
                    favoriteGunCombo = "SCAR + MAG-7",
                    characterSkillActive = "Dimitri",
                    characterSkillPassives = "Thiva + Olivia + Kelly",
                    joinDate = "2023-11-18",
                    notes = "Support & Revive machine."
                ),
                GuildMemberEntity(
                    ign = "IGNT・Draco 🐉",
                    gameUid = "920184710",
                    role = "RECRUIT",
                    rank = "DIAMOND",
                    level = 57,
                    starsOrPoints = 12,
                    todayDogTags = 40,
                    weeklyDogTags = 90,
                    totalGloryEarned = 3200,
                    kdRatio = 2.6,
                    headshotPercentage = 35,
                    winRatePercentage = 25,
                    matchesPlayed = 320,
                    squadGroup = "ROOKIE_SQUAD",
                    combatRole = "RUSHER",
                    activeStatus = "ONLINE",
                    favoriteGunCombo = "Bizon + M1887",
                    characterSkillActive = "Wukong",
                    characterSkillPassives = "Kelly + Hayato + Antonio",
                    joinDate = "2024-02-01",
                    notes = "Trial recruit in training."
                )
            )
            dao.insertAllMembers(initialMembers)

            // Announcements
            val initialAnnouncements = listOf(
                AnnouncementEntity(
                    title = "🔥 FRIDAY DOG TAG TOURNAMENT RUSH!",
                    content = "Today is Friday! Target is 1800 Dog Tags to unlock Custom Room Cards for everyone in the guild! Every member must grind at least 80+ dog tags in Clash Squad or Battle Royale matches today. Let's Booyah together!",
                    category = "DOG_TAG_RUSH",
                    authorRole = "LEADER",
                    authorIgn = "亗 IGNITE・Viper 亗",
                    isPinned = true
                ),
                AnnouncementEntity(
                    title = "⚔️ Tier-1 Guild Scrim vs TOTAL GAMING ARMY",
                    content = "Official 6-Match BR Scrim scheduled for 8:00 PM tonight. Squad Alpha (Viper, Shadow, Ragnar, Phoenix) be in Discord Voice 15 mins prior. Map Rotation: Bermuda -> Purgatory -> Kalahari -> Alpine -> NexTerra -> Bermuda.",
                    category = "SCRIM_ALERT",
                    authorRole = "OFFICER",
                    authorIgn = "IGNT・Ragnar ⚡",
                    isPinned = true
                ),
                AnnouncementEntity(
                    title = "💎 Weekly Dog Tag MVP Giveaway Winners",
                    content = "Congratulations to Viper (260 tags), Shadow (220 tags) & Ghost (210 tags)! 100 Diamond Airdrops have been sent. Keep grinding for next week's rewards!",
                    category = "REWARD",
                    authorRole = "LEADER",
                    authorIgn = "亗 IGNITE・Viper 亗",
                    isPinned = false
                ),
                AnnouncementEntity(
                    title = "📢 Inactivity Policy & Purge Notice",
                    content = "Members offline for 3+ consecutive days without notice or logging 0 dog tags on Friday will face demotion or kick to make room for active recruits.",
                    category = "GENERAL",
                    authorRole = "LEADER",
                    authorIgn = "亗 IGNITE・Viper 亗",
                    isPinned = false
                )
            )
            dao.insertAllAnnouncements(initialAnnouncements)

            // Scrim Matches
            val initialScrims = listOf(
                ScrimMatchEntity(
                    title = "Grand Clash CS 4v4 vs GODLIKE FF",
                    opponentGuild = "GODLIKE ESPORTS",
                    mode = "CLASH_SQUAD",
                    map = "BERMUDA",
                    scheduledTime = "Tonight, 9:00 PM IST",
                    roomId = "84920194",
                    roomPassword = "777",
                    squadAssigned = "CS_DOMINATORS",
                    status = "UPCOMING",
                    result = "PENDING",
                    customRules = "Gun Property OFF, Character Skill ON, Limited Ammo YES, Grenade Limit 1"
                ),
                ScrimMatchEntity(
                    title = "Daily Tier-1 BR Scrim - Match 1",
                    opponentGuild = "TSG ARMY",
                    mode = "BATTLE_ROYALE_SQUAD",
                    map = "PURGATORY",
                    scheduledTime = "Yesterday, 8:30 PM",
                    roomId = "39102948",
                    roomPassword = "456",
                    squadAssigned = "SQUAD_ALPHA",
                    status = "COMPLETED",
                    result = "WIN",
                    ourScore = 32,
                    opponentScore = 18,
                    totalKills = 14,
                    placementPoints = 12,
                    mvpIgn = "亗 IGNITE・Viper 亗",
                    proofNotes = "Dominated final circle in Brasilia. Viper 6 kills, Shadow 4 kills sniper cover."
                ),
                ScrimMatchEntity(
                    title = "Weekend Showdown vs MAFIA CLAN",
                    opponentGuild = "MAFIA GANG",
                    mode = "BATTLE_ROYALE_SQUAD",
                    map = "KALAHARI",
                    scheduledTime = "Tomorrow, 7:00 PM",
                    roomId = "",
                    roomPassword = "",
                    squadAssigned = "SQUAD_BRAVO",
                    status = "UPCOMING",
                    result = "PENDING",
                    customRules = "Standard Competitive Rules, Full Gun Attributes OFF"
                )
            )
            dao.insertAllScrims(initialScrims)

            // Recruitment Applications
            val initialApplications = listOf(
                RecruitmentEntity(
                    applicantIgn = "亗 DEVIL・Rider 亗",
                    applicantUid = "492018392",
                    rank = "MASTER",
                    level = 68,
                    kdRatio = 4.1,
                    headshotRate = 62,
                    discordTag = "devil_rider#4412",
                    preferredRole = "RUSHER",
                    reason = "Ex-Officer of Team Vortex looking for top tier competitive guild for tournaments."
                ),
                RecruitmentEntity(
                    applicantIgn = "FF_SniperQueen",
                    applicantUid = "301948291",
                    rank = "HEROIC",
                    level = 61,
                    kdRatio = 3.5,
                    headshotRate = 58,
                    discordTag = "queen_ff#8821",
                    preferredRole = "SNIPER",
                    reason = "Grinding CS and BR daily. Active every Friday for 100+ dog tags."
                )
            )
            dao.insertAllApplications(initialApplications)

            // Rewards
            val initialRewards = listOf(
                GuildRewardEntity(
                    title = "Friday Dog Tag Champion",
                    rewardType = "DIAMOND_AIRDROP",
                    recipientIgn = "亗 IGNITE・Viper 亗",
                    reason = "Earned 260 Dog Tags in single Friday session",
                    dateAwarded = "2024-03-08"
                ),
                GuildRewardEntity(
                    title = "Tournament Scrim MVP",
                    rewardType = "WEEKLY_MEMBERSHIP",
                    recipientIgn = "IGNT・Shadow 𝄟",
                    reason = "14 Kills in Grand Finals against TSG Army",
                    dateAwarded = "2024-03-01"
                )
            )
            dao.insertAllRewards(initialRewards)
        }
    }
}
