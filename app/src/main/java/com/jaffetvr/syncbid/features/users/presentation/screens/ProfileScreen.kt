package com.jaffetvr.syncbid.features.users.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.users.presentation.components.SyncBidBottomNav
import com.jaffetvr.syncbid.features.users.presentation.components.userBottomNavItems

@Composable
fun ProfileScreen(
    onNavigateToRoute: (String) -> Unit
) {
    Scaffold(
        containerColor = Black2,
        bottomBar = {
            SyncBidBottomNav(
                items = userBottomNavItems,
                currentRoute = "config",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Perfil",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Próximamente…",
                    fontSize = 14.sp,
                    color = White30
                )
            }
        }
    }
}
