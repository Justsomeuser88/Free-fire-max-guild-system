package com.example.ui.screens.recruitment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.local.entity.RecruitmentEntity
import com.example.domain.model.GameRank
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GuildViewModel

@Composable
fun RecruitmentScreen(
    viewModel: GuildViewModel
) {
    val context = LocalContext.current
    val applications by viewModel.allApplications.collectAsState()
    val profile by viewModel.guildProfile.collectAsState()

    var showNewApplicantDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = FFDarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewApplicantDialog = true },
                containerColor = FFCyanAccent,
                contentColor = FFDarkBackground,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_applicant")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Applicant")
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
                            text = "GUILD RECRUITMENT",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = FFTextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Trial Prospects, Applications & Requirements",
                            style = MaterialTheme.typography.labelSmall.copy(color = FFCyanAccent)
                        )
                    }

                    IconButton(
                        onClick = {
                            val poster = """
                                📢【${profile.guildName} IS RECRUITING!】📢
                                🔥 Level ${profile.guildLevel} Top Tier Free Fire Guild
                                
                                🎯 REQUIREMENTS:
                                • Minimum Rank: ${profile.minRankRequirement}+
                                • Minimum K/D Ratio: ${profile.minKdRequirement}+
                                • Minimum Level: ${profile.minLevelRequirement}+
                                • Friday Dog Tags: 80+ mandatory
                                • Mic + Active in Guild Scrims
                                
                                💎 GUILD PERKS:
                                • Weekly Custom Room Cards guaranteed (1800 Dog Tags)
                                • Daily Tier-1 Scrims & Tournaments
                                • Diamond Airdrops & Fire Pass Giveaways
                                
                                📩 Apply with your UID & Stats:
                                Discord: ${profile.discordLink}
                                WhatsApp: ${profile.whatsappGroup}
                            """.trimIndent()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Recruitment Notice", poster)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Recruitment Notice copied for social media / WhatsApp!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("export_recruitment_poster_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Poster",
                            tint = FFFireGold
                        )
                    }
                }
            }

            // Requirements Card
            item {
                FFCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = FFCyanAccent.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = "📋 GUILD JOINING REQUIREMENTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = FFCyanAccent
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(FFDarkSurfaceVariant)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Min Rank", color = FFTextMuted, fontSize = 9.sp)
                            Text(profile.minRankRequirement, color = FFFireGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Min K/D", color = FFTextMuted, fontSize = 9.sp)
                            Text("${profile.minKdRequirement}", color = FFFireOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Min Level", color = FFTextMuted, fontSize = 9.sp)
                            Text("Lvl ${profile.minLevelRequirement}", color = FFEmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Applications List
            item {
                Text(
                    text = "PENDING APPLICATIONS (${applications.size})",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = FFTextPrimary
                    )
                )
            }

            if (applications.isEmpty()) {
                item {
                    FFCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No pending applications right now. Tap + to log trial players.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = FFTextMuted)
                        )
                    }
                }
            } else {
                items(applications, key = { it.id }) { app ->
                    ApplicantCard(
                        application = app,
                        onApprove = {
                            viewModel.approveApplication(app)
                            Toast.makeText(context, "${app.applicantIgn} accepted into Guild Roster!", Toast.LENGTH_LONG).show()
                        },
                        onReject = {
                            viewModel.rejectApplication(app)
                            Toast.makeText(context, "Application rejected.", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteApplication(app)
                            Toast.makeText(context, "Application removed.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showNewApplicantDialog) {
        NewApplicantDialog(
            onDismiss = { showNewApplicantDialog = false },
            onAdd = { newApp ->
                viewModel.addApplication(newApp)
                showNewApplicantDialog = false
                Toast.makeText(context, "Application logged for ${newApp.applicantIgn}!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ApplicantCard(
    application: RecruitmentEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDelete: () -> Unit
) {
    val isPending = application.status == "PENDING"
    val isApproved = application.status == "APPROVED"

    FFCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isApproved) FFEmeraldGreen.copy(alpha = 0.5f) else (if (isPending) FFCyanAccent.copy(alpha = 0.4f) else FFDarkBorder)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header Row: IGN, Status, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = application.applicantIgn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = FFTextPrimary
                        )
                    )
                    Text(
                        text = "UID: ${application.applicantUid} • Role: ${application.preferredRole}",
                        style = MaterialTheme.typography.labelSmall.copy(color = FFTextMuted, fontSize = 10.sp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (application.status) {
                                    "APPROVED" -> FFEmeraldGreen.copy(alpha = 0.2f)
                                    "REJECTED" -> FFFireRed.copy(alpha = 0.2f)
                                    else -> FFCyanAccent.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = application.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (application.status) {
                                    "APPROVED" -> FFEmeraldGreen
                                    "REJECTED" -> FFFireRed
                                    else -> FFCyanAccent
                                },
                                fontSize = 10.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("delete_application_${application.id}")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = FFTextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Stats Matrix
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(FFDarkSurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("Level ${application.level}", color = FFFireGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(application.rank, color = FFCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("KD: ${application.kdRatio}", color = FFFireOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("HS: ${application.headshotRate}%", color = FFEmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Application Note
            Text(
                text = "\"${application.reason}\"",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = FFTextSecondary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )

            // Approve / Reject Buttons if Pending
            if (isPending) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = FFEmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("approve_applicant_${application.id}")
                    ) {
                        Text("Accept to Guild ✓", color = FFDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FFFireRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(34.dp).testTag("reject_applicant_${application.id}")
                    ) {
                        Text("Reject ✕", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun NewApplicantDialog(
    onDismiss: () -> Unit,
    onAdd: (RecruitmentEntity) -> Unit
) {
    var ign by remember { mutableStateOf("") }
    var gameUid by remember { mutableStateOf("") }
    var rank by remember { mutableStateOf("HEROIC") }
    var level by remember { mutableStateOf("60") }
    var kdRatio by remember { mutableStateOf("3.2") }
    var headshotRate by remember { mutableStateOf("48") }
    var discordTag by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Looking for active guild to play scrims.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Trial Applicant",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = FFCyanAccent
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
                        label = { Text("Applicant Free Fire IGN") },
                        modifier = Modifier.fillMaxWidth().testTag("applicant_ign_input"),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = gameUid,
                        onValueChange = { gameUid = it },
                        label = { Text("Player UID") },
                        modifier = Modifier.fillMaxWidth(),
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
                    OutlinedTextField(
                        value = discordTag,
                        onValueChange = { discordTag = it },
                        label = { Text("Discord / Phone (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Trial Notes / Application Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            FFGamingButton(
                text = "Log Applicant",
                onClick = {
                    if (ign.isNotBlank()) {
                        onAdd(
                            RecruitmentEntity(
                                applicantIgn = ign,
                                applicantUid = gameUid.ifBlank { "000000000" },
                                rank = rank,
                                level = level.toIntOrNull() ?: 60,
                                kdRatio = kdRatio.toDoubleOrNull() ?: 3.0,
                                headshotRate = headshotRate.toIntOrNull() ?: 45,
                                discordTag = discordTag,
                                reason = reason
                            )
                        )
                    }
                },
                enabled = ign.isNotBlank(),
                testTag = "submit_applicant_button"
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FFTextSecondary) }
        },
        containerColor = FFDarkSurfaceCard
    )
}
