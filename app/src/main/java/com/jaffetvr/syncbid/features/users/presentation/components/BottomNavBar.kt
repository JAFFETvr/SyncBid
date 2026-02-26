package com.jaffetvr.syncbid.features.users.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.*

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val userBottomNavItems = listOf(
    BottomNavItem("Panel", Icons.Outlined.Home, "dashboard"),
    BottomNavItem("Crear", Icons.Outlined.AddBox, "create"),
    BottomNavItem("Inventario", Icons.Outlined.Inventory2, "inventory"),
    BottomNavItem("Config", Icons.Outlined.Settings, "config")
)

@Composable
fun SyncBidBottomNav(
    items: List<BottomNavItem>,
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
        items.forEach { item ->
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
