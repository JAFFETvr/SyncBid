package com.jaffetvr.syncbid.features.users.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.InventoryViewModel
import com.jaffetvr.syncbid.features.users.presentation.components.SyncBidBottomNav
import com.jaffetvr.syncbid.features.users.presentation.components.userBottomNavItems

@Composable
fun FavoritesScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = SnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.errors.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val statusFilters: List<Pair<InventoryStatus?, String>> = buildList {
        add(null to "Todas")
        val activeCount = uiState.items.count { it.status == InventoryStatus.ACTIVE }
        val pendingCount = uiState.items.count { it.status == InventoryStatus.PENDING }
        val endedCount = uiState.items.count { it.status == InventoryStatus.ENDED }
        add(InventoryStatus.ACTIVE to "Activa ($activeCount)")
        add(InventoryStatus.PENDING to "Pendiente ($pendingCount)")
        if (endedCount > 0) add(InventoryStatus.ENDED to "Finalizada ($endedCount)")
    }

    Scaffold(
        containerColor = Black2,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SyncBidBottomNav(
                items = userBottomNavItems,
                currentRoute = "inventory",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ─── Header ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventario",
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
                        text = "${uiState.items.size} ITEMS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // ─── Status Filter Tabs ───
            LazyRow(
                modifier = Modifier.padding(bottom = 10.dp),
                contentPadding = PaddingValues(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusFilters) { (status, label) ->
                    val selected = uiState.selectedStatus == status
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) GoldSubtle else Black3)
                            .then(
                                if (selected) Modifier.border(
                                    1.dp,
                                    GoldBorder,
                                    RoundedCornerShape(20.dp)
                                ) else Modifier
                            )
                            .clickable { viewModel.filterByStatus(status) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Gold else White50,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            // ─── Content ───
            if (uiState.isLoading && uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Gold,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.filteredItems, key = { it.id }) { item ->
                        InventoryCard(item = item)
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }
    }
}

@Composable
private fun InventoryCard(
    item: InventoryItem,
    modifier: Modifier = Modifier
) {
    val statusColor = when (item.status) {
        InventoryStatus.ACTIVE -> GreenLight
        InventoryStatus.PENDING -> Gold
        InventoryStatus.ENDED -> White50
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
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ─── Image placeholder ───
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Black4),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.take(2).uppercase(),
                color = White30,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // ─── Info ───
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = item.name,
                color = White90,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Base $${String.format("%,.0f", item.basePrice)}",
                    color = White30,
                    fontSize = 11.sp
                )
                if (item.bidCount > 0) {
                    Text(
                        text = "·",
                        color = White30,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${item.bidCount} pujas",
                        color = White50,
                        fontSize = 11.sp
                    )
                }
            }
            item.scheduledAt?.let { time ->
                Text(
                    text = time,
                    color = White30,
                    fontSize = 10.sp
                )
            }
        }

        // ─── Status badge ───
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = statusLabel,
                color = statusColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
