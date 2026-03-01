package com.jaffetvr.syncbid.features.users.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.users.presentation.components.SyncBidBottomNav
import com.jaffetvr.syncbid.features.users.presentation.components.userBottomNavItems
import com.jaffetvr.syncbid.features.users.presentation.viewModels.ProfileUiEvent
import com.jaffetvr.syncbid.features.users.presentation.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToRoute: (String) -> Unit,
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileUiEvent.NavigateToLogin -> onLogout()
            }
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Perfil",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 18.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GoldSubtle)
                            .border(2.dp, GoldBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.initials,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = uiState.username,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                    if (uiState.email.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.email,
                            fontSize = 13.sp,
                            color = White50
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileInfoRow(
                    icon = Icons.Outlined.Person,
                    label = "Usuario",
                    value = uiState.username
                )
                ProfileInfoRow(
                    icon = Icons.Outlined.Email,
                    label = "Correo",
                    value = uiState.email
                )
                if (uiState.createdAt.isNotBlank()) {
                    ProfileInfoRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Miembro desde",
                        value = uiState.createdAt
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = viewModel::onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedSubtle,
                    contentColor = RedLight
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar sesión",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Black3)
            .border(1.dp, White06, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = White30,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                color = White90,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
