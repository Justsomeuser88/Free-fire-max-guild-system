package com.example.ui.screens.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.GuildProfileEntity
import com.example.domain.model.AnnouncementCategory
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun DashboardScreen(
    viewModel: GuildViewModel,
    onNavigateToRoster: () -> Unit,
    onNavigateToDogTags: () -> Unit,
    onNavigateToScrims: () -> Unit,
    onNavigateToLineup: () -> Unit,
    onNavigateToRecruitment: () -> Unit,
    onNavigateToTools: () -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.guildProfile.collectAsState()
    val members by viewModel.allMembers.collectAsState()
    val announcements by viewModel.allAnnouncements.collectAsState()
    val scrims by viewModel.allScrims.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FFDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Guild Banner & Hero Card
        item {
            GuildHeroBanner(
                profile = profile,
                onEditClick = { showEditProfileDialog = true },
                onCopyUid = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Guild UID", profile.guildUid)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Guild UID copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Friday Dog Tag Rush Card
        item {
            FridayDogTagBanner(
                profile = profile,
                onOpenTracker = onNavigateToDogTags,
                onClaimCard = {
                    if (profile.fridayDogTagsTotal >= profile.fridayDogTagsTarget) {
                        viewModel.claimRoomCards()
                        Toast.makeText(context, "🎉 2x Custom Room Cards Claimed for Guild!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Collect 1800 Dog Tags to claim room cards!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Quick Stats Matrix
        item {
            val totalMembers = members.size
            val activeMembers = members.count { it.activeStatus == "ONLINE" || it.activeStatus == "IN_GAME" }
            val completedScrims = scrims.filter { it.status == "COMPLETED" }
            val wins = completedScrims.count { it.result == "WIN" }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Roster Capacity",
                    value = "$totalMembers/50",
                    subtitle = "$activeMembers Active Now",
                    icon = Icons.Default.People,
                    accentColor = FFCyanAccent,
                    onClick = onNavigateToRoster
                )
                QuickStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Scrim Booyah",
                    value = "$wins/${completedScrims.size}",
                    subtitle = if (completedScrims.isNotEmpty()) "${(wins * 100 / completedScrims.size)}% Win Rate" else "Ready",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = FFFireGold,
                    onClick = onNavigateToScrims
                )
            }
        }

        // Quick Navigation Tiles
        item {
            Text(
                text = "⚡ QUICK GUILD OPERATIONS",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = FFFireAmber,
                    letterSpacing = 1.2.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickOpTile(
                    modifier = Modifier.weight(1f),
                    title = "Lineups",
                    icon = Icons.Default.Groups,
                    color = FFFireOrange,
                    onClick = onNavigateToLineup
                )
                QuickOpTile(
                    modifier = Modifier.weight(1f),
                    title = "Friday Tags",
                    icon = Icons.Default.MilitaryTech,
                    color = FFFireGold,
                    onClick = onNavigateToDogTags
                )
                QuickOpTile(
                    modifier = Modifier.weight(1f),
                    title = "Recruits",
                    icon = Icons.Default.PersonAdd,
                    color = FFCyanAccent,
                    onClick = onNavigateToRecruitment
                )
                QuickOpTile(
                    modifier = Modifier.weight(1f),
                    title = "FF Tools",
                    icon = Icons.Default.Handyman,
                    color = FFPurpleAccent,
                    onClick = onNavigateToTools
                )
            }
        }

        // Guild Announcements & Notices
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📢 GUILD BULLETIN & WAR ALERTS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = FFTextPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                }
                IconButton(
                    onClick = { showAddAnnouncementDialog = true },
                    modifier = Modifier.testTag("add_announcement_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Post Announcement",
                        tint = FFFireOrange
                    )
                }
            }
        }

        if (announcements.isEmpty()) {
            item {
                FFCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No notices posted yet. Tap + to broadcast an alert!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = FFTextMuted)
                    )
                }
            }
        } else {
            items(announcements, key = { it.id }) { announcement ->
                AnnouncementItemCard(
                    announcement = announcement,
                    onTogglePin = { viewModel.toggleAnnouncementPin(announcement) },
                    onDelete = { viewModel.deleteAnnouncement(announcement) },
                    onShare = {
                        val shareText = "【${profile.guildName} NOTICE】\n📌 ${announcement.title}\n\n${announcement.content}\n\n— Posted by ${announcement.authorIgn}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Guild Notice", shareText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied notice for WhatsApp/Discord!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showEditProfileDialog) {
        EditGuildProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateGuildProfile(updated)
                showEditProfileDialog = false
                Toast.makeText(context, "Guild Profile updated!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddAnnouncementDialog) {
        AddAnnouncementDialog(
            authorIgn = profile.leaderIgn,
            onDismiss = { showAddAnnouncementDialog = false },
            onAdd = { title, content, category, isPinned ->
                viewModel.addAnnouncement(
                    title = title,
                    content = content,
                    category = category,
                    authorRole = "LEADER",
                    authorIgn = profile.leaderIgn,
                    isPinned = isPinned
                )
                showAddAnnouncementDialog = false
                Toast.makeText(context, "Broadcast posted to Guild Bulletin!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun GuildHeroBanner(
    profile: GuildProfileEntity,
    onEditClick: () -> Unit,
    onCopyUid: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2C1608),
                        Color(0xFF1B1F33),
                        Color(0xFF141724)
                    )
                )
            )
            .border(1.5.dp, Brush.horizontalGradient(listOf(FFFireOrange, FFFireGold, FFCyanAccent)), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Level Badge & Slogan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFFireGold)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LVL ${profile.guildLevel}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = FFDarkBackground
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FFFireOrange.copy(alpha = 0.2f))
                            .border(0.8.dp, FFFireOrange, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "[${profile.guildTag}]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFFireOrangeLight
                            )
                        )
                    }
                    Text(
                        text = profile.region,
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted)
                    )
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("edit_guild_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Guild Profile",
                        tint = FFTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Guild Name
            Text(
                text = profile.guildName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = FFTextPrimary,
                    letterSpacing = 0.5.sp
                )
            )

            // Slogan
            Text(
                text = "\"${profile.slogan}\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = FFTextGold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Glory Progress Bar
            val gloryRatio = if (profile.targetGlory > 0) {
                profile.currentGlory.toFloat() / profile.targetGlory.toFloat()
            } else 0f

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Guild Glory",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary)
                    )
                    Text(
                        text = "${profile.currentGlory.formatNumber()} / ${profile.targetGlory.formatNumber()} Glory (Lvl ${profile.guildLevel + 1})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFFireGold
                        )
                    )
                }
                FFProgressBar(progress = gloryRatio, height = 8.dp)
            }

            Divider(color = FFDarkBorder.copy(alpha = 0.6f), thickness = 0.8.dp)

            // UID & Leader info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCopyUid() }
                        .background(FFDarkSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "UID: ${profile.guildUid}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFCyanAccent
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy UID",
                        tint = FFCyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Leader:",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted)
                    )
                    Text(
                        text = profile.leaderIgn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFFireGold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun FridayDogTagBanner(
    profile: GuildProfileEntity,
    onOpenTracker: () -> Unit,
    onClaimCard: () -> Unit
) {
    val progress = (profile.fridayDogTagsTotal.toFloat() / profile.fridayDogTagsTarget.toFloat()).coerceIn(0f, 1f)
    val isUnlocked = profile.fridayDogTagsTotal >= profile.fridayDogTagsTarget

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF26190B),
                        Color(0xFF1E1C2B)
                    )
                )
            )
            .border(1.dp, FFFireGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onOpenTracker() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🐕", fontSize = 18.sp)
                    Text(
                        text = "FRIDAY DOG TAG TOURNAMENT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = FFFireGold
                        )
                    )
                }
                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FFEmeraldGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "ROOM CARD UNLOCKED! 🃏",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFEmeraldGreen
                            )
                        )
                    }
                } else {
                    Text(
                        text = "${profile.fridayDogTagsTarget - profile.fridayDogTagsTotal} tags left",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFFireOrange)
                    )
                }
            }

            FFProgressBar(
                progress = progress,
                brush = Brush.horizontalGradient(listOf(FFFireOrange, FFFireGold, FFEmeraldGreen)),
                height = 10.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${profile.fridayDogTagsTotal} / ${profile.fridayDogTagsTarget} Dog Tags",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FFTextPrimary
                    )
                )

                if (isUnlocked && !profile.roomCardsClaimed) {
                    Button(
                        onClick = onClaimCard,
                        colors = ButtonDefaults.buttonColors(containerColor = FFFireGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("claim_room_card_button")
                    ) {
                        Text(
                            text = "CLAIM 2x ROOM CARD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = FFDarkBackground
                            )
                        )
                    }
                } else if (profile.roomCardsClaimed) {
                    Text(
                        text = "✓ Claimed in Mailbox",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFEmeraldGreen)
                    )
                } else {
                    Text(
                        text = "Tap to manage contributions >",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFCyanAccent)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    FFCard(
        modifier = modifier,
        onClick = onClick,
        borderColor = accentColor.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = FFTextPrimary
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                color = accentColor
            )
        )
    }
}

@Composable
fun QuickOpTile(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(FFDarkSurface)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = FFTextPrimary
            ),
            maxLines = 1
        )
    }
}

@Composable
fun AnnouncementItemCard(
    announcement: AnnouncementEntity,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val category = try {
        AnnouncementCategory.valueOf(announcement.category)
    } catch (e: Exception) {
        AnnouncementCategory.GENERAL
    }

    FFCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (announcement.isPinned) FFFireGold.copy(alpha = 0.6f) else FFDarkBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = category.icon, fontSize = 14.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(category.tagColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = category.tagColor,
                            fontSize = 10.sp
                        )
                    )
                }
                if (announcement.isPinned) {
                    Text(
                        text = "📌 PINNED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = FFFireGold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Row {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(32.dp).testTag("share_announcement_${announcement.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = FFCyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (announcement.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        tint = if (announcement.isPinned) FFFireGold else FFTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_announcement_${announcement.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = FFFireRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = announcement.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = FFTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = announcement.content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = FFTextSecondary,
                lineHeight = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Posted by ${announcement.authorIgn} (${announcement.authorRole})",
            style = MaterialTheme.typography.labelSmall.copy(
                color = FFTextMuted,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun EditGuildProfileDialog(
    currentProfile: GuildProfileEntity,
    onDismiss: () -> Unit,
    onSave: (GuildProfileEntity) -> Unit
) {
    var guildName by remember { mutableStateOf(currentProfile.guildName) }
    var guildTag by remember { mutableStateOf(currentProfile.guildTag) }
    var guildUid by remember { mutableStateOf(currentProfile.guildUid) }
    var slogan by remember { mutableStateOf(currentProfile.slogan) }
    var region by remember { mutableStateOf(currentProfile.region) }
    var leaderIgn by remember { mutableStateOf(currentProfile.leaderIgn) }
    var minRank by remember { mutableStateOf(currentProfile.minRankRequirement) }
    var minKd by remember { mutableStateOf(currentProfile.minKdRequirement.toString()) }
    var minLevel by remember { mutableStateOf(currentProfile.minLevelRequirement.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Guild Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireGold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = guildName,
                    onValueChange = { guildName = it },
                    label = { Text("Guild Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_guild_name_input"),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = guildTag,
                        onValueChange = { guildTag = it },
                        label = { Text("Tag (4-5 chars)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = guildUid,
                        onValueChange = { guildUid = it },
                        label = { Text("Guild UID") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = leaderIgn,
                    onValueChange = { leaderIgn = it },
                    label = { Text("Leader In-Game Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = slogan,
                    onValueChange = { slogan = it },
                    label = { Text("Guild Slogan") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Server / Region") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Save Changes",
                onClick = {
                    onSave(
                        currentProfile.copy(
                            guildName = guildName.ifBlank { currentProfile.guildName },
                            guildTag = guildTag.ifBlank { currentProfile.guildTag },
                            guildUid = guildUid.ifBlank { currentProfile.guildUid },
                            slogan = slogan,
                            region = region,
                            leaderIgn = leaderIgn.ifBlank { currentProfile.leaderIgn },
                            minRankRequirement = minRank,
                            minKdRequirement = minKd.toDoubleOrNull() ?: currentProfile.minKdRequirement,
                            minLevelRequirement = minLevel.toIntOrNull() ?: currentProfile.minLevelRequirement
                        )
                    )
                },
                testTag = "save_guild_profile_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FFTextSecondary)
            }
        },
        containerColor = FFDarkSurfaceCard
    )
}

@Composable
fun AddAnnouncementDialog(
    authorIgn: String,
    onDismiss: () -> Unit,
    onAdd: (title: String, content: String, category: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AnnouncementCategory.GENERAL.name) }
    var isPinned by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Broadcast Guild Notice",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireOrange
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Headline / Title") },
                    modifier = Modifier.fillMaxWidth().testTag("announcement_title_input"),
                    singleLine = true
                )

                // Category selector
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary)
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(AnnouncementCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat.name,
                            onClick = { selectedCategory = cat.name },
                            label = { Text("${cat.icon} ${cat.displayName}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = cat.tagColor.copy(alpha = 0.3f),
                                selectedLabelColor = cat.tagColor
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Announcement Details") },
                    modifier = Modifier.fillMaxWidth().height(110.dp).testTag("announcement_content_input"),
                    maxLines = 5
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = CheckboxDefaults.colors(checkedColor = FFFireGold)
                    )
                    Text(
                        text = "Pin to top of Bulletin",
                        style = MaterialTheme.typography.bodySmall.copy(color = FFTextPrimary)
                    )
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Post Alert",
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onAdd(title, content, selectedCategory, isPinned)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                testTag = "submit_announcement_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FFTextSecondary)
            }
        },
        containerColor = FFDarkSurfaceCard
    )
}

// Utility extension for formatting numbers
fun Int.formatNumber(): String {
    return if (this >= 1000) {
        String.format("%.1fk", this / 1000f)
    } else {
        this.toString()
    }
}
