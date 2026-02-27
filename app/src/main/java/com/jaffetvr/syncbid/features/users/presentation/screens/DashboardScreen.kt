package com.jaffetvr.syncbid.features.users.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.users.presentation.components.*
import com.jaffetvr.syncbid.features.users.presentation.viewModels.DashboardUiEvent
import com.jaffetvr.syncbid.features.users.presentation.viewModels.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToRoute: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DashboardUiEvent.NavigateToDetail -> onNavigateToDetail(event.auctionId)
                is DashboardUiEvent.ShowError -> { /* handled via state */ }
            }
        }
    }

    Scaffold(
        containerColor = Black2,
        bottomBar = {
            SyncBidBottomNav(
                items = userBottomNavItems,
                currentRoute = "dashboard",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ─── Header: Saludo + Avatar ───
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Buenos días",
                        fontSize = 11.sp,
                        color = White30
                    )
                    Text(
                        text = uiState.userName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Black4, CircleShape)
                        .border(1.dp, GoldBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.userName.take(2).uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold
                    )
                }
            }

            // ─── Live Strip ───
            LiveStrip(
                liveCount = uiState.liveCount,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ─── Filter Tabs ───
            FilterTabs(
                categories = uiState.categories,
                selected = uiState.selectedCategory,
                onSelected = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (uiState.isLoading && uiState.filteredAuctions.isEmpty()) {
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredAuctions,
                        key = { it.id }
                    ) { auction ->
                        AuctionCard(
                            auction = auction,
                            onClick = { viewModel.onAuctionClick(auction.id) }
                        )
                    }
                }
            }
        }
    }
}