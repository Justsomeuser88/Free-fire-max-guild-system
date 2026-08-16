package com.example.ui.screens.dogtags

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.local.entity.GuildMemberEntity
import com.example.domain.model.MemberRole
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun DogTagTrackerScreen(
    viewModel: GuildViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.guildProfile.collectAsState()
    val members by viewModel.allMembers.collectAsState()

    val sortedMembers = remember(members) {
        members.sortedByDescending { it.todayDogTags }
    }

    var selectedMemberForCustomTags by remember { mutableStateOf<GuildMemberEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FFDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Friday Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF2E1909),
                                Color(0xFF1E1724),
                                Color(0xFF141724)
                            )
                        )
                    )
                    .border(1.2.dp, FFFireGold, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🐕", fontSize = 20.sp)
                                Text(
                                    text = "FRIDAY GUILD TOURNAMENT",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = FFFireGold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                            Text(
                                text = "Collect 1800 Dog Tags to unlock Custom Room Cards!",
                                style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                            )
                        }

                        IconButton(
                            onClick = {
                                val report = StringBuilder()
                                report.append("🐕【${profile.guildName} - FRIDAY DOG TAG REPORT】🐕\n")
                                report.append("Total Progress: ${profile.fridayDogTagsTotal} / ${profile.fridayDogTagsTarget} Tags\n")
                                report.append("Status: ${if (profile.fridayDogTagsTotal >= 1800) "🎉 ROOM CARDS UNLOCKED!" else "Grind in progress!"}\n\n")
                                report.append("🏆 TOP CONTRIBUTORS:\n")
                                sortedMembers.take(10).forEachIndexed { index, m ->
                                    val medal = when(index) { 0 -> "🥇"; 1 -> "🥈"; 2 -> "🥉"; else -> "${index+1}." }
                                    report.append("$medal ${m.ign} — ${m.todayDogTags} tags\n")
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Dog Tag Report", report.toString())
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Dog Tag Report copied for WhatsApp!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("export_dogtag_report_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Report",
                                tint = FFFireOrange
                            )
                        }
                    }

                    // Progress Bar
                    val ratio = (profile.fridayDogTagsTotal.toFloat() / profile.fridayDogTagsTarget.toFloat()).coerceIn(0f, 1f)
                    FFProgressBar(
                        progress = ratio,
                        height = 14.dp,
                        brush = Brush.horizontalGradient(listOf(FFFireOrange, FFFireGold, FFEmeraldGreen))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${profile.fridayDogTagsTotal} / 1800 Tags",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFTextPrimary
                            )
                        )
                        val remaining = (1800 - profile.fridayDogTagsTotal).coerceAtLeast(0)
                        Text(
                            text = if (remaining == 0) "TARGET ACHIEVED! 🏆" else "$remaining Tags Remaining",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (remaining == 0) FFEmeraldGreen else FFFireGold
                            )
                        )
                    }

                    // Milestone Chests
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MilestoneBadge(target = 400, label = "Resupply Map", current = profile.fridayDogTagsTotal)
                        MilestoneBadge(target = 800, label = "Bonfire x3", current = profile.fridayDogTagsTotal)
                        MilestoneBadge(target = 1200, label = "Airdrop x3", current = profile.fridayDogTagsTotal)
                        MilestoneBadge(target = 1800, label = "ROOM CARD", current = profile.fridayDogTagsTotal, isGold = true)
                    }
                }
            }
        }

        // Leaderboard Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 MEMBER DOG TAG LEADERBOARD",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = FFTextPrimary,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "Individual Target: 80+ tags",
                    style = MaterialTheme.typography.labelSmall.copy(color = FFFireOrange)
                )
            }
        }

        // Leaderboard List
        itemsIndexed(sortedMembers, key = { _, m -> m.id }) { index, member ->
            DogTagMemberItem(
                rankIndex = index + 1,
                member = member,
                onAddTags = { tags ->
                    viewModel.addDogTagsToMember(member.id, tags)
                    Toast.makeText(context, "+$tags tags added to ${member.ign}!", Toast.LENGTH_SHORT).show()
                },
                onOpenCustom = {
                    selectedMemberForCustomTags = member
                }
            )
        }
    }

    selectedMemberForCustomTags?.let { member ->
        CustomDogTagDialog(
            member = member,
            onDismiss = { selectedMemberForCustomTags = null },
            onAdd = { tags ->
                viewModel.addDogTagsToMember(member.id, tags)
                selectedMemberForCustomTags = null
                Toast.makeText(context, "+$tags tags added to ${member.ign}!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun MilestoneBadge(
    target: Int,
    label: String,
    current: Int,
    isGold: Boolean = false
) {
    val reached = current >= target
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (reached) (if (isGold) FFFireGold else FFEmeraldGreen) else FFDarkSurfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (reached) "✓" else "$target",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = if (reached) FFDarkBackground else FFTextMuted,
                    fontSize = 10.sp
                )
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = if (reached) (if (isGold) FFFireGold else FFEmeraldGreen) else FFTextMuted
            ),
            maxLines = 1
        )
    }
}

@Composable
fun DogTagMemberItem(
    rankIndex: Int,
    member: GuildMemberEntity,
    onAddTags: (Int) -> Unit,
    onOpenCustom: () -> Unit
) {
    val meetsTarget = member.todayDogTags >= 80

    FFCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (rankIndex <= 3) FFFireGold.copy(alpha = 0.5f) else FFDarkBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Rank number / medal
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            when (rankIndex) {
                                1 -> FFFireGold
                                2 -> Color(0xFFCFD8DC)
                                3 -> Color(0xFFCD7F32)
                                else -> FFDarkSurfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$rankIndex",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = if (rankIndex <= 3) FFDarkBackground else FFTextSecondary
                        )
                    )
                }

                Column {
                    Text(
                        text = member.ign,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFTextPrimary
                        ),
                        maxLines = 1
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Lvl ${member.level} • ${member.role}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = FFTextMuted,
                                fontSize = 10.sp
                            )
                        )
                        if (meetsTarget) {
                            Text(
                                text = "✓ Qualified (80+)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = FFEmeraldGreen,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }

            // Dog Tag Count & Add Buttons
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${member.todayDogTags} 🐕",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = if (meetsTarget) FFFireGold else FFTextPrimary
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onAddTags(8) },
                        colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+8", fontSize = 10.sp, color = FFFireOrange)
                    }
                    Button(
                        onClick = { onAddTags(16) },
                        colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+16", fontSize = 10.sp, color = FFFireGold)
                    }
                    Button(
                        onClick = onOpenCustom,
                        colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+...", fontSize = 10.sp, color = FFCyanAccent)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDogTagDialog(
    member: GuildMemberEntity,
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    var tagAmount by remember { mutableStateOf("40") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Dog Tags: ${member.ign}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireGold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Current Today: ${member.todayDogTags} Dog Tags",
                    style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                )
                OutlinedTextField(
                    value = tagAmount,
                    onValueChange = { tagAmount = it },
                    label = { Text("Dog Tags to Add") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_dog_tag_input"),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { tagAmount = "20" }, modifier = Modifier.weight(1f)) { Text("+20") }
                    Button(onClick = { tagAmount = "40" }, modifier = Modifier.weight(1f)) { Text("+40") }
                    Button(onClick = { tagAmount = "100" }, modifier = Modifier.weight(1f)) { Text("+100") }
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Add Tags",
                onClick = {
                    val amount = tagAmount.toIntOrNull() ?: 0
                    if (amount > 0) onAdd(amount)
                },
                testTag = "submit_custom_dog_tags"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}
