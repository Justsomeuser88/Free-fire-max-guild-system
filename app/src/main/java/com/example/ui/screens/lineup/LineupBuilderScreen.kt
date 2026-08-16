package com.example.ui.screens.lineup

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.GuildMemberEntity
import com.example.domain.model.CombatRole
import com.example.domain.model.SquadType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun LineupBuilderScreen(
    viewModel: GuildViewModel
) {
    val context = LocalContext.current
    val members by viewModel.allMembers.collectAsState()
    val profile by viewModel.guildProfile.collectAsState()

    var selectedSquad by remember { mutableStateOf(SquadType.SQUAD_ALPHA) }
    var selectedMemberForAssign by remember { mutableStateOf<GuildMemberEntity?>(null) }
    var showAssignDialog by remember { mutableStateOf(false) }

    val squadMembers = remember(members, selectedSquad) {
        members.filter { it.squadGroup == selectedSquad.name }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FFDarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SQUAD LINEUP BUILDER",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = FFTextPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Esports Rosters, Roles & Character Combos",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFFireOrange)
                    )
                }

                IconButton(
                    onClick = {
                        val squadText = StringBuilder()
                        squadText.append("🏆【${profile.guildName} — ${selectedSquad.squadName}】🏆\n")
                        squadMembers.forEachIndexed { idx, m ->
                            squadText.append("Slot ${idx + 1}: ${m.ign} [${m.combatRole}]\n")
                            squadText.append("  • UID: ${m.gameUid} | Lvl ${m.level} ${m.rank}\n")
                            squadText.append("  • Skills: ${m.characterSkillActive} + ${m.characterSkillPassives}\n")
                            squadText.append("  • Gun: ${m.favoriteGunCombo}\n\n")
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Squad Lineup", squadText.toString())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Squad lineup copied for tournament registration!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("share_squad_lineup_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Lineup",
                        tint = FFFireGold
                    )
                }
            }
        }

        // Squad Selector Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listOf(SquadType.SQUAD_ALPHA, SquadType.SQUAD_BRAVO, SquadType.CS_DOMINATORS, SquadType.ROOKIE_SQUAD)) { sq ->
                    FilterChip(
                        selected = selectedSquad == sq,
                        onClick = { selectedSquad = sq },
                        label = { Text(sq.squadName, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FFFireOrange.copy(alpha = 0.25f),
                            selectedLabelColor = FFFireOrange
                        )
                    )
                }
            }
        }

        // Squad Summary Card
        item {
            val avgKd = if (squadMembers.isNotEmpty()) {
                String.format("%.2f", squadMembers.map { it.kdRatio }.average())
            } else "0.00"
            val avgHs = if (squadMembers.isNotEmpty()) {
                "${squadMembers.map { it.headshotPercentage }.average().toInt()}%"
            } else "0%"

            FFCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = FFFireOrange.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selectedSquad.squadName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = FFFireGold
                            )
                        )
                        Text(
                            text = selectedSquad.description,
                            style = MaterialTheme.typography.bodySmall.copy(color = FFTextSecondary)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFCyanAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${squadMembers.size}/5 Assigned",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFCyanAccent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("Avg Squad K/D: $avgKd", color = FFFireOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Avg Headshot: $avgHs", color = FFCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Add Member to this Squad Button
        item {
            Button(
                onClick = { showAssignDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("assign_member_to_squad_button")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = FFFireGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assign Player to ${selectedSquad.squadName}", color = FFTextPrimary, fontSize = 12.sp)
            }
        }

        // Squad Slots List
        if (squadMembers.isEmpty()) {
            item {
                FFCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No players assigned to this lineup yet. Tap above to assign guild members.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = FFTextMuted)
                    )
                }
            }
        } else {
            items(squadMembers) { member ->
                LineupSlotCard(
                    member = member,
                    onRemoveFromSquad = {
                        viewModel.updateMemberSquad(member, SquadType.UNASSIGNED.name)
                        Toast.makeText(context, "Removed ${member.ign} from ${selectedSquad.squadName}", Toast.LENGTH_SHORT).show()
                    },
                    onChangeCombatRole = { newRole ->
                        viewModel.updateMember(member.copy(combatRole = newRole))
                        Toast.makeText(context, "${member.ign} role changed to $newRole", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showAssignDialog) {
        val availableMembers = members.filter { it.squadGroup != selectedSquad.name }
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = {
                Text(
                    text = "Assign Player to ${selectedSquad.squadName}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = FFFireGold
                    )
                )
            },
            text = {
                if (availableMembers.isEmpty()) {
                    Text("All members are already assigned to this squad.", color = FFTextMuted)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableMembers) { m ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FFDarkSurfaceVariant)
                                    .clickable {
                                        viewModel.updateMemberSquad(m, selectedSquad.name)
                                        showAssignDialog = false
                                        Toast.makeText(context, "Assigned ${m.ign} to ${selectedSquad.squadName}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(m.ign, color = FFTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Lvl ${m.level} • ${m.rank} • KD ${m.kdRatio}", color = FFTextMuted, fontSize = 10.sp)
                                }
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = FFFireOrange)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignDialog = false }) { Text("Close", color = FFTextSecondary) }
            },
            containerColor = FFDarkSurfaceCard
        )
    }
}

@Composable
fun LineupSlotCard(
    member: GuildMemberEntity,
    onRemoveFromSquad: () -> Unit,
    onChangeCombatRole: (String) -> Unit
) {
    FFCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: IGN, Combat Role, Remove Button
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(FFFireGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.combatRole.take(1),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = FFFireGold
                            )
                        )
                    }

                    Column {
                        Text(
                            text = member.ign,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFTextPrimary
                            )
                        )
                        Text(
                            text = "UID: ${member.gameUid} • Lvl ${member.level} ${member.rank}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = FFTextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onRemoveFromSquad,
                    modifier = Modifier.size(28.dp).testTag("remove_from_squad_${member.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = FFFireRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Role Selector Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(CombatRole.values()) { r ->
                    FilterChip(
                        selected = member.combatRole.equals(r.name, ignoreCase = true),
                        onClick = { onChangeCombatRole(r.name) },
                        label = { Text("${r.iconText} ${r.roleName}", fontSize = 9.sp) }
                    )
                }
            }

            // Character Skills & Guns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FFDarkSurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Skills:", color = FFTextMuted, fontSize = 9.sp)
                    Text("★ ${member.characterSkillActive} (Active)", color = FFCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("+ ${member.characterSkillPassives}", color = FFTextSecondary, fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Weapons:", color = FFTextMuted, fontSize = 9.sp)
                    Text(member.favoriteGunCombo, color = FFFireGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
