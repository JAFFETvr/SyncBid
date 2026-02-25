package com.jaffetvr.syncbid.features.admin.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.Black
import com.jaffetvr.syncbid.core.ui.theme.Black2
import com.jaffetvr.syncbid.core.ui.theme.Black3
import com.jaffetvr.syncbid.core.ui.theme.Gold
import com.jaffetvr.syncbid.core.ui.theme.GoldBorder
import com.jaffetvr.syncbid.core.ui.theme.GoldSubtle
import com.jaffetvr.syncbid.core.ui.theme.White70
import com.jaffetvr.syncbid.core.ui.theme.White90
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryStatus
import com.jaffetvr.syncbid.features.admin.presentation.components.InventoryItemCard
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onBack: () -> Unit,
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
        containerColor = Black,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Inventario", color = White90, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Black2)
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
                CircularProgressIndicator(color = Gold)
            }
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
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
                                        .background(
                                            if (selected) GoldSubtle else Black3
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
                }
            }
        }
    }
}
