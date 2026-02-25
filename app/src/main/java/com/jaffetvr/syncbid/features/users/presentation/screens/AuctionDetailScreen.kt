package com.jaffetvr.syncbid.features.users.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.users.presentation.components.formatTime
import com.jaffetvr.syncbid.features.users.presentation.viewModels.AuctionDetailUiEvent
import com.jaffetvr.syncbid.features.users.presentation.viewModels.AuctionDetailViewModel
import com.jaffetvr.syncbid.features.users.presentation.viewModels.BidStatus
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuctionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = SnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AuctionDetailUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is AuctionDetailUiEvent.ShowSuccess -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val auction = uiState.auction

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Black2,
        topBar = {
            TopAppBar(
                title = { Text("Subasta", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = White.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* favorito */ }) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = White.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Black2,
                    titleContentColor = White90
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading || auction == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Gold, strokeWidth = 2.dp)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ─── Hero Image ───
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Black4),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Laptop,
                    contentDescription = null,
                    tint = White.copy(alpha = 0.08f),
                    modifier = Modifier.size(64.dp)
                )
                // Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color.Transparent,
                                    Black2
                                ),
                                startY = 90f
                            )
                        )
                )
                // Gallery dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(5.dp)
                            .background(Gold, RoundedCornerShape(3.dp))
                    )
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(White30, RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            // ─── Scrollable Content ───
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Text(
                    text = auction.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (uiState.isRolledBack) "Estado revertido" else auction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.isRolledBack) RedLight else White30
                )
                Spacer(modifier = Modifier.height(14.dp))

                // ─── Stats Grid ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Precio
                    StatBox(
                        value = "$${String.format("%,.0f", auction.currentPrice)}",
                        label = when (uiState.bidStatus) {
                            BidStatus.PROCESSING -> "Enviando…"
                            BidStatus.ERROR -> "↩ Revertido"
                            else -> "Precio"
                        },
                        valueColor = GoldLight,
                        labelColor = when (uiState.bidStatus) {
                            BidStatus.ERROR -> RedLight
                            else -> Gold
                        },
                        borderColor = when (uiState.bidStatus) {
                            BidStatus.ERROR -> RedBorder
                            else -> GoldBorder
                        },
                        modifier = Modifier.weight(1f),
                        opacity = if (uiState.bidStatus == BidStatus.PROCESSING) 0.5f else 1f
                    )
                    // Timer
                    StatBox(
                        value = formatTime(auction.timeRemainingSeconds),
                        label = "Tiempo",
                        valueColor = if (auction.timeRemainingSeconds < 30) RedLight else White90,
                        borderColor = if (auction.timeRemainingSeconds < 30) RedBorder else White06,
                        modifier = Modifier.weight(1f)
                    )
                    // Pujas
                    StatBox(
                        value = "${auction.bidCount}",
                        label = "Pujas",
                        modifier = Modifier.weight(1f),
                        opacity = if (uiState.bidStatus == BidStatus.PROCESSING) 0.5f else 1f
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ─── Leader Row ───
                val leaderBg = when {
                    uiState.isRolledBack -> RedSubtle
                    auction.isUserWinning -> GreenSubtle
                    else -> White03
                }
                val leaderBorder = when {
                    uiState.isRolledBack -> RedBorder
                    auction.isUserWinning -> GreenBorder
                    else -> White06
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(leaderBg, RoundedCornerShape(12.dp))
                        .border(1.dp, leaderBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Black4, CircleShape)
                            .border(1.dp, leaderBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (auction.leaderName ?: "?").take(2).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (uiState.isRolledBack) RedLight else GreenLight
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                uiState.bidStatus == BidStatus.PROCESSING -> "Actualizando…"
                                else -> "${auction.leaderName ?: "Desconocido"}${if (auction.isUserWinning) " (Tú)" else ""}"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = White90
                        )
                        Text(
                            text = when {
                                uiState.bidStatus == BidStatus.PROCESSING -> "Esperando confirmación"
                                uiState.isRolledBack -> "Superó tu puja"
                                else -> "Postor líder"
                            },
                            fontSize = 10.sp,
                            color = White30
                        )
                    }
                    // Badge
                    if (uiState.bidStatus == BidStatus.PROCESSING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 1.5.dp,
                            color = White30
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (uiState.isRolledBack) RedSubtle else GreenSubtle,
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (uiState.isRolledBack) RedBorder else GreenBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Líder",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.isRolledBack) RedLight else GreenLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ─── Incremento rápido ───
                Text(
                    text = "INCREMENTO RÁPIDO",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = White30,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val increments = listOf(10.0, 50.0, 100.0)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    increments.forEach { amount ->
                        val isSelected = uiState.selectedIncrement == amount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) GoldSubtle else White03,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) GoldBorder else White06,
                                    RoundedCornerShape(12.dp)
                                )
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(
                                    enabled = uiState.bidStatus != BidStatus.PROCESSING
                                ) {
                                    viewModel.onIncrementSelected(amount)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$${amount.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) GoldLight else White90
                            )
                        }
                    }
                }
            }

            // ─── Bid Button (bottom) ───
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                // Snackbar de error (si hay rollback)
                if (uiState.isRolledBack && uiState.error != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Black4, RoundedCornerShape(12.dp))
                            .border(1.dp, RedBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = RedLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Error de sincronización",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = White90
                            )
                            Text(
                                text = "Puja revertida · Conflicto de concurrencia",
                                fontSize = 10.sp,
                                color = White30
                            )
                        }
                        Text(
                            text = "Reintentar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gold,
                            modifier = Modifier.clickable { viewModel.onRetryBid() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val bidAmount = (auction.currentPrice + uiState.selectedIncrement)
                val isProcessing = uiState.bidStatus == BidStatus.PROCESSING

                Button(
                    onClick = viewModel::onPlaceBid,
                    enabled = !isProcessing,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isProcessing) Black4 else White,
                        contentColor = if (isProcessing) White30 else Black,
                        disabledContainerColor = Black4,
                        disabledContentColor = White30
                    ),
                    border = if (isProcessing) ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(White12)
                    ) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 1.5.dp,
                            color = White30
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Procesando puja…",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    } else {
                        Text(
                            text = "Pujar ahora · $${String.format("%,.0f", bidAmount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = White90,
    labelColor: androidx.compose.ui.graphics.Color = White30,
    borderColor: androidx.compose.ui.graphics.Color = White06,
    opacity: Float = 1f
) {
    Column(
        modifier = modifier
            .background(White03, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(10.dp)
            .then(if (opacity < 1f) Modifier.background(White03.copy(alpha = opacity)) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 14.sp),
            color = valueColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = labelColor,
            letterSpacing = 0.5.sp
        )
    }
}
