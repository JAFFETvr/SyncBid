package com.jaffetvr.syncbid.features.admin.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.Black3
import com.jaffetvr.syncbid.core.ui.theme.Gold
import com.jaffetvr.syncbid.core.ui.theme.GoldBorder
import com.jaffetvr.syncbid.core.ui.theme.GoldSubtle
import com.jaffetvr.syncbid.core.ui.theme.GreenLight
import com.jaffetvr.syncbid.core.ui.theme.White70
import com.jaffetvr.syncbid.core.ui.theme.White90
import com.jaffetvr.syncbid.features.admin.domain.entities.ActivityEvent
import com.jaffetvr.syncbid.features.admin.domain.entities.ActivityType
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus

@Composable
fun KpiCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Black3)
            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                color = White90,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = label,
                color = White70,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun KpiGrid(
    liveAuctions: Int,
    activeBids: Int,
    totalRevenue: Double,
    onlineUsers: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                label = "Subastas activas",
                value = "$liveAuctions",
                icon = Icons.Default.Gavel,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                label = "Pujas activas",
                value = "$activeBids",
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                label = "Ingresos",
                value = "$${String.format("%,.0f", totalRevenue)}",
                icon = Icons.Default.AttachMoney,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                label = "Usuarios en línea",
                value = "$onlineUsers",
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ActivityItem(
    event: ActivityEvent,
    modifier: Modifier = Modifier
) {
    val accentColor = when (event.type) {
        ActivityType.BID_WON -> GreenLight
        ActivityType.AUCTION_CREATED -> Gold
        ActivityType.AUCTION_ENDED -> White70
        ActivityType.USER_REGISTERED -> GoldSubtle
        ActivityType.ERROR -> androidx.compose.ui.graphics.Color(0xFFC0392B)
    }
    val icon = when (event.type) {
        ActivityType.BID_WON -> Icons.Default.TrendingUp
        ActivityType.AUCTION_CREATED -> Icons.Default.Gavel
        ActivityType.AUCTION_ENDED -> Icons.Default.Inventory
        ActivityType.USER_REGISTERED -> Icons.Default.People
        ActivityType.ERROR -> Icons.Default.TrendingUp
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Black3)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                color = White90,
                fontSize = 13.sp
            )
            Text(
                text = event.timeAgo,
                color = White70,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (item.status) {
        InventoryStatus.ACTIVE -> GreenLight
        InventoryStatus.PENDING -> Gold
        InventoryStatus.ENDED -> White70
    }
    val statusLabel = when (item.status) {
        InventoryStatus.ACTIVE -> "Activa"
        InventoryStatus.PENDING -> "Pendiente"
        InventoryStatus.ENDED -> "Finalizada"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Black3)
            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = White90, fontSize = 14.sp)
        }
        Text(
            text = "$${String.format("%,.2f", item.currentPrice)}",
            color = Gold,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = statusLabel, color = statusColor, fontSize = 11.sp)
        }
    }
}
