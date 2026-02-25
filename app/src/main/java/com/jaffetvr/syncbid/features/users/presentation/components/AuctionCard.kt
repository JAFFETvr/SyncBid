package com.jaffetvr.syncbid.features.users.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.AuctionStatus

@Composable
fun AuctionCard(
    auction: Auction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (auction.isUserWinning) GreenBorder else White06,
        label = "border"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Black3)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // ─── Imagen / Placeholder ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .background(Black4),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when {
                    auction.name.contains("iPhone", true) -> Icons.Default.Smartphone
                    auction.name.contains("MacBook", true) || auction.name.contains("Laptop", true) -> Icons.Default.Laptop
                    auction.name.contains("Watch", true) -> Icons.Default.Watch
                    else -> Icons.Default.Smartphone
                },
                contentDescription = null,
                tint = White12,
                modifier = Modifier.size(40.dp)
            )
            // Overlay gradiente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                Black3
                            ),
                            startY = 50f
                        )
                    )
            )
            // Chip estado
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                StatusChip(
                    isLive = auction.status == AuctionStatus.LIVE,
                    isWinning = auction.isUserWinning
                )
            }
        }

        // ─── Body ───
        Column(modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp)) {
            Text(
                text = auction.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = White90
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${auction.category} · ${auction.bidCount} pujas",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = White30
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Precio + Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = if (auction.isUserWinning) "TU PUJA" else "PRECIO ACTUAL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = White30
                    )
                    Text(
                        text = "$${String.format("%,.0f", auction.currentPrice)}",
                        style = MaterialTheme.typography.displayLarge,
                        color = if (auction.isUserWinning) GreenLight else GoldLight
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    val isWarning = auction.timeRemainingSeconds < 30
                    Text(
                        text = if (isWarning) "ALERTA" else "TIEMPO",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = White30
                    )
                    Text(
                        text = formatTime(auction.timeRemainingSeconds),
                        style = MaterialTheme.typography.displayMedium,
                        color = if (isWarning) RedLight else White90
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(isLive: Boolean, isWinning: Boolean) {
    val bg: Color
    val border: Color
    val textColor: Color
    val label: String

    if (isWinning) {
        bg = GreenSubtle
        border = GreenBorder
        textColor = GreenLight
        label = "Ganando"
    } else {
        bg = RedSubtle
        border = RedBorder
        textColor = RedLight
        label = "En vivo"
    }

    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (!isWinning && isLive) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(RedLight, RoundedCornerShape(3.dp))
            )
        }
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            letterSpacing = 0.8.sp
        )
    }
}

fun formatTime(seconds: Long): String {
    val min = seconds / 60
    val sec = seconds % 60
    return String.format("%02d:%02d", min, sec)
}
