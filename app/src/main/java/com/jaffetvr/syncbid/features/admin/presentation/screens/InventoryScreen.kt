package com.jaffetvr.syncbid.features.admin.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.jaffetvr.syncbid.core.ui.theme.Black2
import com.jaffetvr.syncbid.core.ui.theme.Black3
import com.jaffetvr.syncbid.core.ui.theme.Gold
import com.jaffetvr.syncbid.core.ui.theme.GoldBorder
import com.jaffetvr.syncbid.core.ui.theme.GoldSubtle
import com.jaffetvr.syncbid.core.ui.theme.White
import com.jaffetvr.syncbid.core.ui.theme.White30
import com.jaffetvr.syncbid.core.ui.theme.White70
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus
import com.jaffetvr.syncbid.features.admin.presentation.components.AdminBottomNav
import com.jaffetvr.syncbid.features.admin.presentation.components.InventoryItemCard
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.InventoryViewModel

@Composable
fun InventoryScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errors.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val statusFilters: List<Pair<InventoryStatus?, String>> = listOf(
        null to "Todas",
        InventoryStatus.ACTIVE to "Activas",
        InventoryStatus.PENDING to "Pendientes",
        InventoryStatus.ENDED to "Finalizadas"
    )

    Scaffold(
        containerColor = Black2,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AdminBottomNav(
                currentRoute = "admin_inventory",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.items.isEmpty()) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                            text = "Subastas",
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.filteredItems.size} items",
                            fontSize = 12.sp,
                            color = White30
                        )
                    }
                }

                // Status filter tabs
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(statusFilters) { pair ->
                            val status = pair.first
                            val label = pair.second
                            val selected = uiState.selectedStatus == status
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selected) GoldSubtle else Black3)
                                    .border(
                                        1.dp,
                                        if (selected) Gold else GoldBorder,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { viewModel.filterByStatus(status) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) Gold else White70,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                items(uiState.filteredItems, key = { it.id }) { item ->
                    InventoryItemCard(
                        item = item,
                        onClick = { /* navigate to detail */ }
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}
