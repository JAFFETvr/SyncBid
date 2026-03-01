package com.jaffetvr.syncbid.features.admin.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.Black
import com.jaffetvr.syncbid.core.ui.theme.Black3
import com.jaffetvr.syncbid.core.ui.theme.Black4
import com.jaffetvr.syncbid.core.ui.theme.Gold
import com.jaffetvr.syncbid.core.ui.theme.GoldBorder
import com.jaffetvr.syncbid.core.ui.theme.GoldSubtle
import com.jaffetvr.syncbid.core.ui.theme.GreenLight
import com.jaffetvr.syncbid.core.ui.theme.White06
import com.jaffetvr.syncbid.core.ui.theme.White30
import com.jaffetvr.syncbid.core.ui.theme.White50
import com.jaffetvr.syncbid.core.ui.theme.White70
import com.jaffetvr.syncbid.core.ui.theme.White90
import com.jaffetvr.syncbid.features.admin.domain.entities.ActivityEvent
import com.jaffetvr.syncbid.features.admin.domain.entities.ActivityType
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus
import com.jaffetvr.syncbid.features.users.presentation.components.BottomNavItem

val adminBottomNavItems = listOf(
    BottomNavItem("Panel", Icons.Outlined.Dashboard, "admin_dashboard"),
    BottomNavItem("Crear", Icons.Outlined.AddBox, "admin_create"),
    BottomNavItem("Subastas", Icons.Outlined.Inventory2, "admin_inventory"),
    BottomNavItem("Config", Icons.Outlined.Settings, "admin_config")
)

@Composable
fun AdminBottomNav(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Black.copy(alpha = 0.95f))
            .border(width = 1.dp, color = White06)
            .padding(top = 10.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        adminBottomNavItems.forEach { item ->
            val isActive = item.route == currentRoute
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onItemClick(item.route) }
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (isActive) Gold else White30,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) Gold else White30,
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}

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
        ActivityType.ERROR -> Color(0xFFC0392B)
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
        InventoryStatus.ACTIVE -> "ACTIVA"
        InventoryStatus.PENDING -> "PENDIENTE"
        InventoryStatus.ENDED -> "FINALIZADA"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Black3)
            .border(1.dp, White06, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Black4),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.take(2).uppercase(),
                color = White30,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = item.name, color = White90, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "$${String.format("%,.0f", item.currentPrice ?: item.basePrice)}",
                    color = Gold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (item.bidCount > 0) {
                    Text(text = "·", color = White30, fontSize = 12.sp)
                    Text(text = "${item.bidCount} pujas", color = White50, fontSize = 11.sp)
                }
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = statusLabel, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
    }
}
