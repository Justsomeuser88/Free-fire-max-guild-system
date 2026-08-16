package com.example.ui.screens.roster

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
import com.example.data.local.entity.GuildMemberEntity
import com.example.domain.model.*
import com.example.ui.components.*
import com.example.ui.screens.dashboard.formatNumber
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun RosterScreen(
    viewModel: GuildViewModel
) {
    val context = LocalContext.current
    val members by viewModel.filteredMembers.collectAsState()
    val allMembers by viewModel.allMembers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRoleFilter by viewModel.selectedRoleFilter.collectAsState()
    val selectedRankFilter by viewModel.selectedRankFilter.collectAsState()

    var showAddMemberDialog by remember { mutableStateOf(false) }
    var selectedMemberForDetail by remember { mutableStateOf<GuildMemberEntity?>(null) }
    var selectedMemberForEdit by remember { mutableStateOf<GuildMemberEntity?>(null) }

    Scaffold(
        containerColor = FFDarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMemberDialog = true },
                containerColor = FFFireOrange,
                contentColor = FFDarkBackground,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_member")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header & Roster stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GUILD ROSTER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = FFTextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${allMembers.size}/50 Members • ${allMembers.count { it.activeStatus == "ONLINE" }} Online",
                            style = MaterialTheme.typography.labelSmall.copy(color = FFCyanAccent)
                        )
                    }

                    // Quick sort / export action
                    IconButton(
                        onClick = {
                            val rosterText = StringBuilder("亗 IGNITE GUILD ROSTER 亗\n")
                            allMembers.forEachIndexed { i, m ->
                                rosterText.append("${i + 1}. [${m.role}] ${m.ign} (UID: ${m.gameUid}) - Lvl ${m.level} ${m.rank} | KD: ${m.kdRatio}\n")
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Guild Roster", rosterText.toString())
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Full roster copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("export_roster_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Roster",
                            tint = FFFireGold
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by IGN or UID...", color = FFTextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = FFFireOrange)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = FFTextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("roster_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FFDarkSurface,
                        unfocusedContainerColor = FFDarkSurface,
                        focusedBorderColor = FFFireOrange,
                        unfocusedBorderColor = FFDarkBorder
                    ),
                    singleLine = true
                )
            }

            // Role Filters
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedRoleFilter == "ALL",
                            onClick = { viewModel.setRoleFilter("ALL") },
                            label = { Text("All Roles (${allMembers.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FFFireOrange.copy(alpha = 0.25f),
                                selectedLabelColor = FFFireOrange
                            )
                        )
                    }
                    items(MemberRole.values()) { role ->
                        val count = allMembers.count { it.role.equals(role.name, ignoreCase = true) }
                        FilterChip(
                            selected = selectedRoleFilter.equals(role.name, ignoreCase = true),
                            onClick = { viewModel.setRoleFilter(role.name) },
                            label = { Text("${role.displayName} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = role.badgeColor.copy(alpha = 0.25f),
                                selectedLabelColor = role.badgeColor
                            )
                        )
                    }
                }
            }

            // Members List
            if (members.isEmpty()) {
                item {
                    FFCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No guild members match your search/filter.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = FFTextMuted)
                        )
                    }
                }
            } else {
                items(members, key = { it.id }) { member ->
                    MemberRosterCard(
                        member = member,
                        onClick = { selectedMemberForDetail = member },
                        onQuickAddDogTags = { tags ->
                            viewModel.addDogTagsToMember(member.id, tags)
                            Toast.makeText(context, "+$tags Dog Tags added to ${member.ign}!", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = { selectedMemberForEdit = member },
                        onCopyUid = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Player UID", member.gameUid)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied UID: ${member.gameUid}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddMemberDialog) {
        AddEditMemberDialog(
            memberToEdit = null,
            onDismiss = { showAddMemberDialog = false },
            onSave = { newMember ->
                viewModel.addMember(newMember)
                showAddMemberDialog = false
                Toast.makeText(context, "Added ${newMember.ign} to Guild Roster!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    selectedMemberForEdit?.let { member ->
        AddEditMemberDialog(
            memberToEdit = member,
            onDismiss = { selectedMemberForEdit = null },
            onSave = { updated ->
                viewModel.updateMember(updated)
                selectedMemberForEdit = null
                Toast.makeText(context, "Updated ${updated.ign}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    selectedMemberForDetail?.let { member ->
        MemberDetailSheet(
            member = member,
            onDismiss = { selectedMemberForDetail = null },
            onPromoteDemote = { newRole ->
                viewModel.updateMemberRole(member, newRole)
                selectedMemberForDetail = member.copy(role = newRole)
                Toast.makeText(context, "${member.ign} role changed to $newRole", Toast.LENGTH_SHORT).show()
            },
            onSquadChange = { newSquad ->
                viewModel.updateMemberSquad(member, newSquad)
                selectedMemberForDetail = member.copy(squadGroup = newSquad)
                Toast.makeText(context, "Assigned to $newSquad", Toast.LENGTH_SHORT).show()
            },
            onAddStrike = {
                viewModel.addStrikeToMember(member)
                selectedMemberForDetail = member.copy(strikesCount = member.strikesCount + 1)
                Toast.makeText(context, "Warning strike recorded!", Toast.LENGTH_SHORT).show()
            },
            onDelete = {
                viewModel.deleteMember(member)
                selectedMemberForDetail = null
                Toast.makeText(context, "Removed from Guild.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun MemberRosterCard(
    member: GuildMemberEntity,
    onClick: () -> Unit,
    onQuickAddDogTags: (Int) -> Unit,
    onEdit: () -> Unit,
    onCopyUid: () -> Unit
) {
    FFCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        borderColor = if (member.role == "LEADER") FFFireGold.copy(alpha = 0.6f) else FFDarkBorder
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Top Row: IGN, Role Badge, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(FFDarkSurfaceVariant)
                            .border(1.dp, FFFireOrange.copy(alpha = 0.4f), CircleShape)
                            .size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.level.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = FFFireGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Column {
                        Text(
                            text = member.ign,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFTextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onCopyUid() }
                        ) {
                            Text(
                                text = "UID: ${member.gameUid}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = FFTextMuted,
                                    fontSize = 11.sp
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy UID",
                                tint = FFTextMuted,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    FFRoleBadge(roleName = member.role)
                    Spacer(modifier = Modifier.height(3.dp))
                    FFStatusIndicator(statusName = member.activeStatus)
                }
            }

            // Middle Row: Rank, Squad, Combat Role
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FFRankBadge(rankName = member.rank)

                if (member.squadGroup != "UNASSIGNED") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FFCyanAccent.copy(alpha = 0.15f))
                            .border(0.8.dp, FFCyanAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = member.squadGroup.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFCyanAccent,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = member.combatRole,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FFTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Stats Matrix: KD, Headshot %, Dog Tags Today, Weekly Glory
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(FFDarkSurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "K/D",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted, fontSize = 9.sp)
                    )
                    Text(
                        text = "${member.kdRatio}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFFireOrange
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Headshot",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted, fontSize = 9.sp)
                    )
                    Text(
                        text = "${member.headshotPercentage}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFCyanAccent
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Dog Tags",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted, fontSize = 9.sp)
                    )
                    Text(
                        text = "${member.todayDogTags} tags",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFFireGold
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Glory",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted, fontSize = 9.sp)
                    )
                    Text(
                        text = member.totalGloryEarned.formatNumber(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FFEmeraldGreen
                        )
                    )
                }
            }

            // Quick Actions Bar: +20 Tags, +40 Tags, Edit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { onQuickAddDogTags(20) },
                        colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("+20 🐕", fontSize = 11.sp, color = FFFireGold, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onQuickAddDogTags(40) },
                        colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("+40 🐕", fontSize = 11.sp, color = FFFireGold, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (member.strikesCount > 0) {
                        Text(
                            text = "⚠️ ${member.strikesCount} Strikes",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = FFFireRed,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp).testTag("edit_member_${member.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Member",
                            tint = FFTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemberDetailSheet(
    member: GuildMemberEntity,
    onDismiss: () -> Unit,
    onPromoteDemote: (String) -> Unit,
    onSquadChange: (String) -> Unit,
    onAddStrike: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = member.ign,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = FFFireGold
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
                        text = "UID: ${member.gameUid} • Joined: ${member.joinDate}",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted)
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FFRoleBadge(roleName = member.role)
                        FFRankBadge(rankName = member.rank)
                    }
                }

                // Combat Loadout
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFDarkSurfaceVariant)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "⚔️ LOADOUT & COMBAT PROFILE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFFireOrange
                            )
                        )
                        Text("Favorite Gun: ${member.favoriteGunCombo}", color = FFTextPrimary, fontSize = 12.sp)
                        Text("Active Skill: ${member.characterSkillActive}", color = FFCyanAccent, fontSize = 12.sp)
                        Text("Passives: ${member.characterSkillPassives}", color = FFTextSecondary, fontSize = 12.sp)
                    }
                }

                // Role Management (Promote / Demote)
                item {
                    Text(
                        text = "Change Guild Role",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MemberRole.values()) { r ->
                            FilterChip(
                                selected = member.role.equals(r.name, ignoreCase = true),
                                onClick = { onPromoteDemote(r.name) },
                                label = { Text(r.displayName, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                // Squad Assignment
                item {
                    Text(
                        text = "Assign Squad Lineup",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary)
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SquadType.values()) { sq ->
                            FilterChip(
                                selected = member.squadGroup.equals(sq.name, ignoreCase = true),
                                onClick = { onSquadChange(sq.name) },
                                label = { Text(sq.squadName, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                // Actions: Strike, Kick
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onAddStrike,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FFFireRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Issue Strike ⚠️", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FFFireRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kick Member ❌", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant)
            ) {
                Text("Close", color = FFTextPrimary)
            }
        },
        containerColor = FFDarkSurfaceCard
    )
}

@Composable
fun AddEditMemberDialog(
    memberToEdit: GuildMemberEntity?,
    onDismiss: () -> Unit,
    onSave: (GuildMemberEntity) -> Unit
) {
    var ign by remember { mutableStateOf(memberToEdit?.ign ?: "") }
    var gameUid by remember { mutableStateOf(memberToEdit?.gameUid ?: "") }
    var level by remember { mutableStateOf(memberToEdit?.level?.toString() ?: "60") }
    var rank by remember { mutableStateOf(memberToEdit?.rank ?: GameRank.HEROIC.name) }
    var role by remember { mutableStateOf(memberToEdit?.role ?: MemberRole.MEMBER.name) }
    var kdRatio by remember { mutableStateOf(memberToEdit?.kdRatio?.toString() ?: "3.0") }
    var headshotRate by remember { mutableStateOf(memberToEdit?.headshotPercentage?.toString() ?: "45") }
    var combatRole by remember { mutableStateOf(memberToEdit?.combatRole ?: CombatRole.RUSHER.name) }
    var squadGroup by remember { mutableStateOf(memberToEdit?.squadGroup ?: SquadType.UNASSIGNED.name) }
    var favoriteGun by remember { mutableStateOf(memberToEdit?.favoriteGunCombo ?: "Woodpecker + MP40") }
    var activeSkill by remember { mutableStateOf(memberToEdit?.characterSkillActive ?: "Tatsuya") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (memberToEdit == null) "Add Guild Member" else "Edit Player Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireGold
                )
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = ign,
                        onValueChange = { ign = it },
                        label = { Text("Free Fire IGN (Name)") },
                        modifier = Modifier.fillMaxWidth().testTag("member_ign_input"),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = gameUid,
                        onValueChange = { gameUid = it },
                        label = { Text("Player UID (Numeric)") },
                        modifier = Modifier.fillMaxWidth().testTag("member_uid_input"),
                        singleLine = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = level,
                            onValueChange = { level = it },
                            label = { Text("Level") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = kdRatio,
                            onValueChange = { kdRatio = it },
                            label = { Text("K/D") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = headshotRate,
                            onValueChange = { headshotRate = it },
                            label = { Text("HS %") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Text("Rank Tier", style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(GameRank.values()) { r ->
                            FilterChip(
                                selected = rank == r.name,
                                onClick = { rank = r.name },
                                label = { Text("${r.badgeSymbol} ${r.displayName}", fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Guild Role", style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(MemberRole.values()) { r ->
                            FilterChip(
                                selected = role == r.name,
                                onClick = { role = r.name },
                                label = { Text(r.displayName, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = favoriteGun,
                        onValueChange = { favoriteGun = it },
                        label = { Text("Favorite Weapons") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = activeSkill,
                        onValueChange = { activeSkill = it },
                        label = { Text("Active Skill Character (Alok, Tatsuya, Chrono...)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = if (memberToEdit == null) "Add Member" else "Save Changes",
                onClick = {
                    if (ign.isNotBlank()) {
                        val base = memberToEdit ?: GuildMemberEntity(ign = ign, gameUid = gameUid)
                        onSave(
                            base.copy(
                                ign = ign,
                                gameUid = gameUid.ifBlank { "000000000" },
                                level = level.toIntOrNull() ?: 60,
                                rank = rank,
                                role = role,
                                kdRatio = kdRatio.toDoubleOrNull() ?: 3.0,
                                headshotPercentage = headshotRate.toIntOrNull() ?: 45,
                                combatRole = combatRole,
                                squadGroup = squadGroup,
                                favoriteGunCombo = favoriteGun,
                                characterSkillActive = activeSkill
                            )
                        )
                    }
                },
                enabled = ign.isNotBlank(),
                testTag = "save_member_button"
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
