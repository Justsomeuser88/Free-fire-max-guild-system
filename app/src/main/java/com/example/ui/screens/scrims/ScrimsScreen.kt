package com.example.ui.screens.scrims

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
import com.example.data.local.entity.ScrimMatchEntity
import com.example.domain.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun ScrimsScreen(
    viewModel: GuildViewModel
) {
    val context = LocalContext.current
    val scrims by viewModel.allScrims.collectAsState()
    val members by viewModel.allMembers.collectAsState()
    val profile by viewModel.guildProfile.collectAsState()
    val selectedFilter by viewModel.selectedScrimFilter.collectAsState()

    var showAddScrimDialog by remember { mutableStateOf(false) }
    var selectedScrimForResult by remember { mutableStateOf<ScrimMatchEntity?>(null) }
    var showRulebookDialog by remember { mutableStateOf(false) }

    val filteredScrims = remember(scrims, selectedFilter) {
        if (selectedFilter == "ALL") scrims
        else scrims.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    Scaffold(
        containerColor = FFDarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddScrimDialog = true },
                containerColor = FFFireRed,
                contentColor = FFTextPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_scrim")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Schedule Scrim")
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
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GUILD WARS & SCRIMS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = FFTextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Manage Custom Rooms, Lineups & Match Results",
                            style = MaterialTheme.typography.labelSmall.copy(color = FFFireOrange)
                        )
                    }

                    Button(
                        onClick = { showRulebookDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FFDarkSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("open_rulebook_button")
                    ) {
                        Text("📜 Rulebook", fontSize = 11.sp, color = FFFireGold, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Status Filter Tabs
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf("ALL", "UPCOMING", "LIVE", "COMPLETED")
                    items(filters) { f ->
                        FilterChip(
                            selected = selectedFilter == f,
                            onClick = { viewModel.setScrimFilter(f) },
                            label = { Text(f) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FFFireRed.copy(alpha = 0.25f),
                                selectedLabelColor = FFFireRed
                            )
                        )
                    }
                }
            }

            // Scrim Match Cards
            if (filteredScrims.isEmpty()) {
                item {
                    FFCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No scrim matches found in this category.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = FFTextMuted)
                        )
                    }
                }
            } else {
                items(filteredScrims, key = { it.id }) { scrim ->
                    ScrimItemCard(
                        scrim = scrim,
                        guildName = profile.guildName,
                        onRecordResult = { selectedScrimForResult = scrim },
                        onDelete = { viewModel.deleteScrim(scrim) },
                        onCopyRoom = {
                            val details = "【${profile.guildName} vs ${scrim.opponentGuild}】\n🎮 Mode: ${scrim.mode}\n🗺️ Map: ${scrim.map}\n⏰ Time: ${scrim.scheduledTime}\n🔑 Room ID: ${scrim.roomId}\n🔒 Password: ${scrim.roomPassword}\n📜 Rules: ${scrim.customRules}"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Scrim Details", details)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Room details & password copied!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showAddScrimDialog) {
        AddScrimDialog(
            onDismiss = { showAddScrimDialog = false },
            onAdd = { newScrim ->
                viewModel.addScrim(newScrim)
                showAddScrimDialog = false
                Toast.makeText(context, "Scrim scheduled vs ${newScrim.opponentGuild}!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    selectedScrimForResult?.let { scrim ->
        RecordMatchResultDialog(
            scrim = scrim,
            members = members,
            onDismiss = { selectedScrimForResult = null },
            onSave = { updated ->
                viewModel.updateScrim(updated)
                selectedScrimForResult = null
                Toast.makeText(context, "Match result recorded!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showRulebookDialog) {
        RulebookGeneratorDialog(
            onDismiss = { showRulebookDialog = false }
        )
    }
}

@Composable
fun ScrimItemCard(
    scrim: ScrimMatchEntity,
    guildName: String,
    onRecordResult: () -> Unit,
    onDelete: () -> Unit,
    onCopyRoom: () -> Unit
) {
    val status = try { MatchStatus.valueOf(scrim.status) } catch (e: Exception) { MatchStatus.UPCOMING }
    val result = try { MatchResult.valueOf(scrim.result) } catch (e: Exception) { MatchResult.PENDING }

    FFCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (scrim.status == "LIVE") FFFireOrange else (if (scrim.result == "WIN") FFEmeraldGreen.copy(alpha = 0.5f) else FFDarkBorder)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Top Row: Mode & Map Badge, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FFFireRed.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = scrim.mode.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFFireRed,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FFDarkSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🗺️ ${scrim.map}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = FFTextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(status.color.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = status.color
                        )
                    )
                }
            }

            // VS Battle Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "OUR GUILD",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFFireGold, fontSize = 10.sp)
                    )
                    Text(
                        text = "亗 IGNITE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = FFTextPrimary
                        )
                    )
                    Text(
                        text = "Lineup: ${scrim.squadAssigned.replace("_", " ")}",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFCyanAccent, fontSize = 10.sp)
                    )
                }

                Text(
                    text = "VS",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = FFFireRed
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "OPPONENT",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted, fontSize = 10.sp)
                    )
                    Text(
                        text = scrim.opponentGuild,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = FFTextPrimary
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "⏰ ${scrim.scheduledTime}",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary, fontSize = 10.sp)
                    )
                }
            }

            // Room ID & Password / Result
            if (scrim.status != "COMPLETED") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Room ID: ${if (scrim.roomId.isNotBlank()) scrim.roomId else "TBA"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFCyanAccent
                            )
                        )
                        Text(
                            text = "Pass: ${if (scrim.roomPassword.isNotBlank()) scrim.roomPassword else "TBA"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFFireGold
                            )
                        )
                    }

                    IconButton(
                        onClick = onCopyRoom,
                        modifier = Modifier.size(24.dp).testTag("copy_room_details_${scrim.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Room",
                            tint = FFCyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            } else {
                // Completed match stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = result.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = result.color
                            )
                        )
                        if (scrim.mvpIgn.isNotBlank()) {
                            Text(
                                text = "👑 MVP: ${scrim.mvpIgn}",
                                style = MaterialTheme.typography.labelSmall.copy(color = FFFireGold)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Score: ${scrim.ourScore} - ${scrim.opponentScore}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FFTextPrimary
                            )
                        )
                        Text(
                            text = "${scrim.totalKills} Kills • ${scrim.placementPoints} Place Pts",
                            style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary)
                        )
                    }
                }
            }

            // Actions: Record Result, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (scrim.status != "COMPLETED") {
                    Button(
                        onClick = onRecordResult,
                        colors = ButtonDefaults.buttonColors(containerColor = FFFireOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("record_result_${scrim.id}")
                    ) {
                        Text("Record Result / Score", fontSize = 11.sp, color = FFDarkBackground, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = scrim.proofNotes.ifBlank { "Match recorded in guild history." },
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_scrim_${scrim.id}")
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
    }
}

@Composable
fun AddScrimDialog(
    onDismiss: () -> Unit,
    onAdd: (ScrimMatchEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var opponentGuild by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("Tonight, 8:30 PM") }
    var mode by remember { mutableStateOf("CLASH_SQUAD") }
    var map by remember { mutableStateOf("BERMUDA") }
    var roomId by remember { mutableStateOf("") }
    var roomPassword by remember { mutableStateOf("") }
    var squadAssigned by remember { mutableStateOf("SQUAD_ALPHA") }
    var rules by remember { mutableStateOf("Gun Attributes OFF, Skills ON, Grenade limit 1, No Roof Camping") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Schedule Scrim / Guild War",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireRed
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
                        value = opponentGuild,
                        onValueChange = { opponentGuild = it },
                        label = { Text("Opponent Guild Name") },
                        modifier = Modifier.fillMaxWidth().testTag("scrim_opponent_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = scheduledTime,
                        onValueChange = { scheduledTime = it },
                        label = { Text("Scheduled Time / Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("Game Mode", style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("CLASH_SQUAD", "BATTLE_ROYALE_SQUAD", "LONE_WOLF")) { m ->
                            FilterChip(
                                selected = mode == m,
                                onClick = { mode = m },
                                label = { Text(m.replace("_", " "), fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Map", style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("BERMUDA", "PURGATORY", "KALAHARI", "ALPINE", "NEXTERRA")) { mp ->
                            FilterChip(
                                selected = map == mp,
                                onClick = { map = mp },
                                label = { Text(mp, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = roomId,
                            onValueChange = { roomId = it },
                            label = { Text("Room ID (Optional)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = roomPassword,
                            onValueChange = { roomPassword = it },
                            label = { Text("Password") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Text("Assigned Lineup", style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("SQUAD_ALPHA", "SQUAD_BRAVO", "CS_DOMINATORS", "ROOKIE_SQUAD")) { sq ->
                            FilterChip(
                                selected = squadAssigned == sq,
                                onClick = { squadAssigned = sq },
                                label = { Text(sq.replace("_", " "), fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = rules,
                        onValueChange = { rules = it },
                        label = { Text("Custom Room Rules") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Create Scrim",
                onClick = {
                    if (opponentGuild.isNotBlank()) {
                        onAdd(
                            ScrimMatchEntity(
                                title = "War vs $opponentGuild",
                                opponentGuild = opponentGuild,
                                mode = mode,
                                map = map,
                                scheduledTime = scheduledTime,
                                roomId = roomId,
                                roomPassword = roomPassword,
                                squadAssigned = squadAssigned,
                                customRules = rules,
                                status = "UPCOMING"
                            )
                        )
                    }
                },
                enabled = opponentGuild.isNotBlank(),
                testTag = "submit_add_scrim_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}

@Composable
fun RecordMatchResultDialog(
    scrim: ScrimMatchEntity,
    members: List<GuildMemberEntity>,
    onDismiss: () -> Unit,
    onSave: (ScrimMatchEntity) -> Unit
) {
    var result by remember { mutableStateOf("WIN") }
    var ourScore by remember { mutableStateOf("7") }
    var opponentScore by remember { mutableStateOf("3") }
    var totalKills by remember { mutableStateOf("18") }
    var placementPoints by remember { mutableStateOf("12") }
    var mvpIgn by remember { mutableStateOf(members.firstOrNull()?.ign ?: "") }
    var proofNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Record Match Score: vs ${scrim.opponentGuild}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireGold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Result Outcome", style = MaterialTheme.typography.labelSmall.copy(color = FFTextSecondary))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = result == "WIN",
                        onClick = { result = "WIN" },
                        label = { Text("BOOYAH (Win) 🏆") },
                        colors = FilterChipDefaults.filterChipColors(selectedLabelColor = FFEmeraldGreen)
                    )
                    FilterChip(
                        selected = result == "LOSS",
                        onClick = { result = "LOSS" },
                        label = { Text("Defeat ❌") },
                        colors = FilterChipDefaults.filterChipColors(selectedLabelColor = FFFireRed)
                    )
                    FilterChip(
                        selected = result == "DRAW",
                        onClick = { result = "DRAW" },
                        label = { Text("Draw 🤝") }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ourScore,
                        onValueChange = { ourScore = it },
                        label = { Text("Our Score/Rounds") },
                        modifier = Modifier.weight(1f).testTag("our_score_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = opponentScore,
                        onValueChange = { opponentScore = it },
                        label = { Text("Opponent Score") },
                        modifier = Modifier.weight(1f).testTag("opponent_score_input"),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalKills,
                        onValueChange = { totalKills = it },
                        label = { Text("Squad Kills") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = mvpIgn,
                        onValueChange = { mvpIgn = it },
                        label = { Text("Match MVP Player") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = proofNotes,
                    onValueChange = { proofNotes = it },
                    label = { Text("Match Notes / Highlights") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Save Match History",
                onClick = {
                    onSave(
                        scrim.copy(
                            status = "COMPLETED",
                            result = result,
                            ourScore = ourScore.toIntOrNull() ?: 0,
                            opponentScore = opponentScore.toIntOrNull() ?: 0,
                            totalKills = totalKills.toIntOrNull() ?: 0,
                            placementPoints = placementPoints.toIntOrNull() ?: 0,
                            mvpIgn = mvpIgn,
                            proofNotes = proofNotes
                        )
                    )
                },
                testTag = "submit_match_result"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}

@Composable
fun RulebookGeneratorDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("FFCS_4V4") }

    val standardCS = """
        亗 OFFICIAL COMPETITIVE CS 4v4 RULES 亗
        1. Gun Attributes: Strictly OFF
        2. Character Skills: ON (No Active skill spam in first 10s)
        3. Limited Ammo: YES
        4. Grenades: Maximum 1 Grenade per player per round
        5. Smoke Grenades: Max 2 per team
        6. Roof / Crane Camping: STRICTLY FORBIDDEN (Disqualification)
        7. Emulator / PC: NOT ALLOWED (Mobile Only)
        8. Friendly Fire: OFF
        9. In-game disconnect: Round will not be replayed unless in first 15 seconds.
    """.trimIndent()

    val standardBR = """
        🏆 OFFICIAL BATTLE ROYALE ESPORTS RULES (FFWS FORMAT) 🏆
        1. Scoring: 
           • 1st Place: 12 pts
           • 2nd Place: 9 pts
           • 3rd Place: 8 pts
           • 4th: 7 pts | 5th: 6 pts | 6th: 5 pts
           • Kill Points: 1 pt per kill
        2. Gun Properties: OFF
        3. Character Skills: ALLOWED
        4. Vehicles: Max 1 per squad in early game
        5. Teaming / Hacks / Glitches: Immediate lifetime tournament ban
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Official Free Fire Rulebook Preset",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFFireGold
                )
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFormat == "FFCS_4V4",
                        onClick = { selectedFormat = "FFCS_4V4" },
                        label = { Text("Clash Squad (CS 4v4)") }
                    )
                    FilterChip(
                        selected = selectedFormat == "FFBR_ESPORTS",
                        onClick = { selectedFormat = "FFBR_ESPORTS" },
                        label = { Text("BR Esports (FFWS)") }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FFDarkSurfaceVariant)
                        .padding(10.dp)
                ) {
                    Text(
                        text = if (selectedFormat == "FFCS_4V4") standardCS else standardBR,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FFTextPrimary,
                            lineHeight = 16.sp,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Copy Rules for Discord/WhatsApp",
                onClick = {
                    val rulesText = if (selectedFormat == "FFCS_4V4") standardCS else standardBR
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("FF Rules", rulesText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Rulebook copied to clipboard!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                testTag = "copy_rules_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}
