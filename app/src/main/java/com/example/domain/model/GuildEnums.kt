package com.example.domain.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class MemberRole(val displayName: String, val badgeColor: Color, val priority: Int) {
    LEADER("Guild Leader", FFFireGold, 1),
    CO_LEADER("Acting Leader", FFFireOrange, 2),
    OFFICER("Guild Officer", FFCyanAccent, 3),
    ELITE("Elite Slayer", FFPurpleAccent, 4),
    MEMBER("Guild Member", FFEmeraldGreen, 5),
    RECRUIT("Trial Recruit", FFTextSecondary, 6)
}

enum class GameRank(val displayName: String, val color: Color, val badgeSymbol: String) {
    GRANDMASTER("Grandmaster", RankGrandmaster, "👑"),
    MASTER("Master", RankMaster, "⭐"),
    HEROIC("Heroic", RankHeroic, "🎖️"),
    DIAMOND("Diamond IV", RankDiamond, "💎"),
    PLATINUM("Platinum IV", RankPlatinum, "🛡️"),
    GOLD("Gold IV", RankGold, "🏅"),
    SILVER("Silver III", RankSilver, "🥈"),
    BRONZE("Bronze", RankBronze, "🥉")
}

enum class ActiveStatus(val displayName: String, val color: Color) {
    ONLINE("In Lobby", FFEmeraldGreen),
    IN_GAME("In Match ⚔️", FFFireOrange),
    RECENTLY("Today", FFCyanAccent),
    INACTIVE("Offline (3d+)", FFFireRed)
}

enum class SquadType(val squadName: String, val description: String) {
    SQUAD_ALPHA("Squad Alpha (Main BR)", "Primary Battle Royale tournament roster"),
    SQUAD_BRAVO("Squad Bravo (Scrims)", "Secondary tier scrims & practice lineup"),
    CS_DOMINATORS("CS Dominators", "Clash Squad 4v4 competitive team"),
    ROOKIE_SQUAD("Academy / Rookies", "Rising trial prospects & recruits"),
    UNASSIGNED("Unassigned", "Not attached to competitive lineup")
}

enum class CombatRole(val roleName: String, val iconText: String) {
    IGL("In-Game Leader (IGL)", "🧠"),
    RUSHER("Entry Rusher", "⚡"),
    SNIPER("Long-Range Sniper", "🎯"),
    FLANKER("Flanker & Cover", "🥷"),
    GRENADIER("Grenadier / Bomber", "💣"),
    SUPPORT("Support / Medic", "💉")
}

enum class AnnouncementCategory(val displayName: String, val tagColor: Color, val icon: String) {
    DOG_TAG_RUSH("Friday Dog Tag Rush", FFFireGold, "🐕"),
    SCRIM_ALERT("Guild War & Scrim", FFFireRed, "⚔️"),
    TOURNAMENT("Esports Tourney", FFPurpleAccent, "🏆"),
    RECRUITMENT("Recruitment Update", FFCyanAccent, "📢"),
    REWARD("Diamonds & Rewards", FFEmeraldGreen, "💎"),
    GENERAL("Guild Notice", FFFireOrange, "📌")
}

enum class ScrimMode(val modeName: String) {
    BATTLE_ROYALE_SQUAD("Battle Royale (BR Squad 48p)"),
    CLASH_SQUAD("Clash Squad (CS 4v4)"),
    LONE_WOLF("Lone Wolf / 2v2 Duel")
}

enum class ScrimMap(val mapName: String) {
    BERMUDA("Bermuda"),
    PURGATORY("Purgatory"),
    KALAHARI("Kalahari"),
    ALPINE("Alpine"),
    NEXTERRA("NexTerra")
}

enum class MatchStatus(val displayName: String, val color: Color) {
    UPCOMING("Scheduled", FFCyanAccent),
    LIVE("Live Now 🔥", FFFireOrange),
    COMPLETED("Finished", FFEmeraldGreen),
    CANCELLED("Cancelled", FFTextMuted)
}

enum class MatchResult(val displayName: String, val color: Color) {
    WIN("BOOYAH! 🏆 (Win)", FFEmeraldGreen),
    LOSS("Defeat ❌", FFFireRed),
    DRAW("Draw 🤝", FFFireGold),
    PENDING("Awaiting Result", FFTextSecondary)
}

enum class RewardType(val displayName: String, val icon: String) {
    DIAMOND_AIRDROP("100 Diamonds Airdrop", "💎"),
    WEEKLY_MEMBERSHIP("Weekly Membership Pass", "🎫"),
    MONTHLY_PASS("Monthly Fire Pass", "👑"),
    ROOM_CARD("Custom Room Card x2", "🃏"),
    GUILD_GIFT_BOX("Guild Elite Gift Box", "🎁")
}
