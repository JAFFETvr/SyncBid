package com.jaffetvr.syncbid.features.admin.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.admin.presentation.components.ActivityItem
import com.jaffetvr.syncbid.features.admin.presentation.components.AdminBottomNav
import com.jaffetvr.syncbid.features.admin.presentation.components.KpiGrid
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.AdminDashboardViewModel

@Composable
fun AdminDashboardScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errors.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = Black2,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AdminBottomNav(
                currentRoute = "admin_dashboard",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.stats == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Gold, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Admin Panel",
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldSubtle)
                                .border(1.dp, GoldBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                uiState.stats?.let { stats ->
                    item {
                        KpiGrid(
                            liveAuctions = stats.liveAuctions,
                            activeBids = stats.activeBids,
                            totalRevenue = stats.totalRevenue,
                            onlineUsers = stats.onlineUsers
                        )
                    }
                }

                item {
                    Text(
                        text = "ACTIVIDAD RECIENTE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White30,
                        letterSpacing = 1.sp
                    )
                }

                items(uiState.recentActivity) { event ->
                    ActivityItem(event = event)
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}
