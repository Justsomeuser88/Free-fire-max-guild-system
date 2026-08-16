package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.*
import com.example.ui.theme.*

@Composable
fun FFCard(
    modifier: Modifier = Modifier,
    borderColor: Color = FFDarkBorder,
    backgroundColor: Color = FFDarkSurface,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable { onClick() }
    } else {
        modifier.clip(shape)
    }

    Column(
        modifier = clickableModifier
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun FFRoleBadge(
    roleName: String,
    modifier: Modifier = Modifier
) {
    val role = try {
        MemberRole.valueOf(roleName.uppercase())
    } catch (e: Exception) {
        MemberRole.MEMBER
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(role.badgeColor.copy(alpha = 0.18f))
            .border(1.dp, role.badgeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(role.badgeColor)
        )
        Text(
            text = role.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = role.badgeColor
            )
        )
    }
}

@Composable
fun FFRankBadge(
    rankName: String,
    modifier: Modifier = Modifier
) {
    val rank = try {
        GameRank.valueOf(rankName.uppercase())
    } catch (e: Exception) {
        GameRank.HEROIC
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(rank.color.copy(alpha = 0.15f))
            .border(0.8.dp, rank.color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = rank.badgeSymbol,
            fontSize = 11.sp
        )
        Text(
            text = rank.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = rank.color
            )
        )
    }
}

@Composable
fun FFStatusIndicator(
    statusName: String,
    modifier: Modifier = Modifier
) {
    val status = try {
        ActiveStatus.valueOf(statusName.uppercase())
    } catch (e: Exception) {
        ActiveStatus.ONLINE
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(status.color)
        )
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = status.color
            )
        )
    }
}

@Composable
fun FFProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    brush: Brush = Brush.horizontalGradient(listOf(FFFireOrange, FFFireGold)),
    trackColor: Color = FFDarkSurfaceVariant
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(safeProgress)
                .clip(RoundedCornerShape(height / 2))
                .background(brush)
        )
    }
}

@Composable
fun FFGamingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "gaming_button",
    icon: (@Composable () -> Unit)? = null,
    gradient: Brush = Brush.horizontalGradient(listOf(FFFireOrange, FFFireGold)),
    textColor: Color = FFDarkBackground,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = FFDarkSurfaceVariant
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .testTag(testTag)
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) gradient else Brush.linearGradient(listOf(FFDarkSurfaceVariant, FFDarkSurfaceVariant)))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (enabled) textColor else FFTextMuted
                )
            )
        }
    }
}
