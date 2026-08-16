package com.example.ui.screens.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GuildRewardEntity
import com.example.domain.model.RewardType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun GuildToolsScreen(
    viewModel: GuildViewModel
) {
    val context = LocalContext.current
    val rewards by viewModel.allRewards.collectAsState()
    val members by viewModel.allMembers.collectAsState()

    var showAddRewardDialog by remember { mutableStateOf(false) }
    var showApkDownloadDialog by remember { mutableStateOf(false) }

    // Stylish Name Generator State
    var rawName by remember { mutableStateOf("VIPER") }
    var rawGuildTag by remember { mutableStateOf("IGNT") }

    // Points Calculator State
    var selectedPlacement by remember { mutableStateOf(1) } // 1 to 12
    var totalKillsInput by remember { mutableStateOf("8") }

    val placementPoints = when (selectedPlacement) {
        1 -> 12
        2 -> 9
        3 -> 8
        4 -> 7
        5 -> 6
        6 -> 5
        7 -> 4
        8 -> 3
        9 -> 2
        10 -> 1
        else -> 0
    }
    val killPoints = totalKillsInput.toIntOrNull() ?: 0
    val totalTournamentScore = placementPoints + killPoints

    val stylizedNicknames = remember(rawName, rawGuildTag) {
        val clean = rawName.trim().ifBlank { "VIPER" }
        val tag = rawGuildTag.trim().ifBlank { "IGNT" }
        listOf(
            "亗 $tag・$clean 亗",
            "𝄟 $tag ᯤ $clean 𝄟",
            "𓊈 $clean 𓊉 ⚡",
            "☬ $tag・$clean ☬",
            "♛ $clean ♛",
            "×͜× $tag・$clean",
            "꧁༺ $clean ༻꧂",
            "⚡ $tag 乡 $clean",
            "炎 $clean 炎",
            "『$tag』$clean ࿐",
            "𖣘 $clean 𖣘",
            "亗 $clean 亗"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FFDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "GUILD TOOLS & UTILITIES",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = FFTextPrimary,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "FF Nickname Styler, Esports Calculator & Rewards",
                    style = MaterialTheme.typography.labelSmall.copy(color = FFPurpleAccent)
                )
            }
        }

        // TOOL 1: Stylish Free Fire Nickname & Guild Tag Generator
        item {
            FFCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = FFFireGold.copy(alpha = 0.5f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✨", fontSize = 18.sp)
                    Text(
                        text = "FREE FIRE NICKNAME & TAG STYLER",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = FFFireGold
                        )
                    )
                }
                Text(
                    text = "Create esports stylized names with clan tags and symbols (亗, 𝄟, 𓊈, ☬)",
                    style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rawGuildTag,
                        onValueChange = { rawGuildTag = it },
                        label = { Text("Guild Tag") },
                        modifier = Modifier.weight(1f).testTag("tag_styler_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = rawName,
                        onValueChange = { rawName = it },
                        label = { Text("Player Name") },
                        modifier = Modifier.weight(2f).testTag("name_styler_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Tap any stylized name to copy to clipboard:", style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted))
                Spacer(modifier = Modifier.height(6.dp))

                stylizedNicknames.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { styled ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FFDarkSurfaceVariant)
                                    .border(0.8.dp, FFDarkBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("FF Name", styled)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Copied: $styled", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = styled,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FFTextPrimary,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // TOOL 2: Competitive Points Calculator (FFWS System)
        item {
            FFCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = FFCyanAccent.copy(alpha = 0.4f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🧮", fontSize = 18.sp)
                    Text(
                        text = "FFWS ESPORTS POINTS CALCULATOR",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = FFCyanAccent
                        )
                    )
                }
                Text(
                    text = "Official Free Fire World Series Scoring (Placement + 1 pt/kill)",
                    style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "$selectedPlacement",
                        onValueChange = {
                            val p = it.toIntOrNull() ?: 1
                            selectedPlacement = p.coerceIn(1, 12)
                        },
                        label = { Text("Placement (#1-#12)") },
                        modifier = Modifier.weight(1f).testTag("placement_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = totalKillsInput,
                        onValueChange = { totalKillsInput = it },
                        label = { Text("Squad Kills") },
                        modifier = Modifier.weight(1f).testTag("kills_input"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Score Output Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Placement Points: $placementPoints pts",
                            style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                        )
                        Text(
                            text = "Kill Points: $killPoints pts",
                            style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("TOTAL MATCH SCORE", color = FFFireGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        Text(
                            text = "$totalTournamentScore PTS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = FFEmeraldGreen
                            )
                        )
                    }
                }
            }
        }

        // TOOL 3: Guild Treasury & Rewards Giveaway History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💎 GUILD REWARDS & GIVEAWAYS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = FFTextPrimary
                    )
                )

                Button(
                    onClick = { showAddRewardDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FFFireOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp).testTag("grant_reward_button")
                ) {
                    Text("+ Grant Reward", fontSize = 11.sp, color = FFDarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (rewards.isEmpty()) {
            item {
                FFCard(modifier = Modifier.fillMaxWidth()) {
                    Text("No rewards logged yet. Tap above to log diamond/pass giveaways.", color = FFTextMuted)
                }
            }
        } else {
            items(rewards, key = { it.id }) { reward ->
                FFCard(modifier = Modifier.fillMaxWidth()) {
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
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FFFireGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💎", fontSize = 16.sp)
                            }

                            Column {
                                Text(
                                    text = reward.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FFTextPrimary
                                    )
                                )
                                Text(
                                    text = "Recipient: ${reward.recipientIgn} • ${reward.dateAwarded}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = FFFireGold, fontSize = 10.sp)
                                )
                                Text(
                                    text = reward.reason,
                                    style = MaterialTheme.typography.bodySmall.copy(color = FFTextMuted, fontSize = 10.sp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteReward(reward) },
                            modifier = Modifier.size(28.dp).testTag("delete_reward_${reward.id}")
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = FFTextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // TOOL 4: Direct APK Download & GitHub Releases
        item {
            FFCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = FFEmeraldGreen.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📲", fontSize = 18.sp)
                        Column {
                            Text(
                                text = "GITHUB DIRECT APK DOWNLOAD",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FFEmeraldGreen
                                )
                            )
                            Text(
                                text = "Install FF Guild Master APK on physical Android phones",
                                style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DownloadForOffline, contentDescription = null, tint = FFEmeraldGreen)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automated CI/CD Workflow Ready",
                            color = FFTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Every push/tag automatically builds FF-Guild-Master.apk via GitHub Actions.",
                            color = FFTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showApkDownloadDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FFEmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp).testTag("open_apk_download_guide_button")
                    ) {
                        Icon(Icons.Default.InstallMobile, contentDescription = null, tint = FFDarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("APK Download Guide", color = FFDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val instructions = """
                                📲【FF GUILD MASTER - DIRECT APK DOWNLOAD】📲
                                
                                1. Open GitHub Releases:
                                   https://github.com/your-username/ff-guild-master/releases
                                2. Download 'FF-Guild-Master.apk' directly under Assets.
                                3. Open the downloaded file on your Android phone and tap Install!
                                *(Make sure 'Install Unknown Apps' is allowed in Settings)*
                            """.trimIndent()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("APK Instructions", instructions)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "APK Download instructions copied!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FFFireGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp).testTag("copy_apk_instructions_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = FFFireGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Link", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (showApkDownloadDialog) {
        ApkDownloadDialog(
            onDismiss = { showApkDownloadDialog = false }
        )
    }

    if (showAddRewardDialog) {
        AddRewardDialog(
            members = members,
            onDismiss = { showAddRewardDialog = false },
            onAdd = { newReward ->
                viewModel.addReward(newReward)
                showAddRewardDialog = false
                Toast.makeText(context, "Reward recorded for ${newReward.recipientIgn}!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AddRewardDialog(
    members: List<com.example.data.local.entity.GuildMemberEntity>,
    onDismiss: () -> Unit,
    onAdd: (GuildRewardEntity) -> Unit
) {
    var title by remember { mutableStateOf("Friday Dog Tag Top Earner Reward") }
    var selectedMember by remember { mutableStateOf(members.firstOrNull()?.ign ?: "Viper") }
    var rewardType by remember { mutableStateOf("DIAMOND_AIRDROP") }
    var reason by remember { mutableStateOf("Earned highest Dog Tags (200+) during Friday rush") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Grant Guild Reward / Giveaway",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireGold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reward Title") },
                    modifier = Modifier.fillMaxWidth().testTag("reward_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = selectedMember,
                    onValueChange = { selectedMember = it },
                    label = { Text("Winner IGN") },
                    modifier = Modifier.fillMaxWidth().testTag("reward_recipient_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Achievement") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Award Reward",
                onClick = {
                    if (title.isNotBlank() && selectedMember.isNotBlank()) {
                        onAdd(
                            GuildRewardEntity(
                                title = title,
                                recipientIgn = selectedMember,
                                rewardType = rewardType,
                                reason = reason,
                                dateAwarded = "Today"
                            )
                        )
                    }
                },
                testTag = "submit_reward_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}

@Composable
fun ApkDownloadDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📲", fontSize = 20.sp)
                Text(
                    text = "Download APK from GitHub",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = FFEmeraldGreen
                    )
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Follow these 3 easy steps to install the app on your physical phone:",
                        style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFDarkSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "1️⃣ Go to GitHub Releases or Actions Tab",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FFFireGold
                                )
                            )
                            Text(
                                text = "Open the repository in your mobile browser or PC.",
                                style = MaterialTheme.typography.bodySmall.copy(color = FFTextPrimary, fontSize = 11.sp)
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFDarkSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "2️⃣ Tap on 'FF-Guild-Master.apk'",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FFFireOrange
                                )
                            )
                            Text(
                                text = "Under Assets in Releases, or download the Artifact from GitHub Actions.",
                                style = MaterialTheme.typography.bodySmall.copy(color = FFTextPrimary, fontSize = 11.sp)
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFDarkSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "3️⃣ Open & Tap Install",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FFEmeraldGreen
                                )
                            )
                            Text(
                                text = "Android will ask to allow 'Install unknown apps'. Enable it to complete installation.",
                                style = MaterialTheme.typography.bodySmall.copy(color = FFTextPrimary, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Copy GitHub Release URL",
                onClick = {
                    val url = "https://github.com/your-username/ff-guild-master/releases/latest"
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Releases URL", url)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "GitHub Releases URL copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                testTag = "copy_release_url_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}

