package com.jaffetvr.syncbid.features.admin.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.jaffetvr.syncbid.core.ui.theme.Black2
import com.jaffetvr.syncbid.core.ui.theme.Black3
import com.jaffetvr.syncbid.core.ui.theme.Gold
import com.jaffetvr.syncbid.core.ui.theme.GoldBorder
import com.jaffetvr.syncbid.core.ui.theme.GoldSubtle
import com.jaffetvr.syncbid.core.ui.theme.Red
import com.jaffetvr.syncbid.core.ui.theme.RedSubtle
import com.jaffetvr.syncbid.core.ui.theme.White
import com.jaffetvr.syncbid.core.ui.theme.White30
import com.jaffetvr.syncbid.core.ui.theme.White70
import com.jaffetvr.syncbid.core.ui.theme.White90
import com.jaffetvr.syncbid.features.admin.presentation.components.AdminBottomNav
import com.jaffetvr.syncbid.features.users.presentation.viewModels.ProfileUiEvent
import com.jaffetvr.syncbid.features.users.presentation.viewModels.ProfileViewModel

@Composable
fun AdminConfigScreen(
    onNavigateToRoute: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileUiEvent.NavigateToLogin -> onLogout()
            }
        }
    }

    Scaffold(
        containerColor = Black2,
        bottomBar = {
            AdminBottomNav(
                currentRoute = "admin_config",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GoldSubtle)
                    .border(2.dp, Gold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.initials.ifEmpty { "A" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.username.ifEmpty { "Admin" },
                style = MaterialTheme.typography.titleLarge,
                color = White,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(GoldSubtle)
                        .border(1.dp, GoldBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ADMINISTRADOR",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info section
            Text(
                text = "INFORMACIÓN DE CUENTA",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = White30,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminInfoRow(
                icon = Icons.Outlined.Person,
                label = "Usuario",
                value = uiState.username.ifEmpty { "—" }
            )
            AdminInfoRow(
                icon = Icons.Outlined.Email,
                label = "Email",
                value = uiState.email.ifEmpty { "—" }
            )
            AdminInfoRow(
                icon = Icons.Outlined.CalendarMonth,
                label = "Registrado",
                value = uiState.createdAt.ifEmpty { "—" }
            )
            AdminInfoRow(
                icon = Icons.Outlined.Shield,
                label = "Rol",
                value = "Administrador"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout button
            Button(
                onClick = viewModel::onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedSubtle,
                    contentColor = Red
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    Icons.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Cerrar sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AdminInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Black3),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = White30
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = White90
            )
        }
    }
}
