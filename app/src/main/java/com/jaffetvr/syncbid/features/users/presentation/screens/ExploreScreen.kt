package com.jaffetvr.syncbid.features.users.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.CreateAuctionEvent
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.CreateAuctionViewModel
import com.jaffetvr.syncbid.features.users.presentation.components.SyncBidBottomNav
import com.jaffetvr.syncbid.features.users.presentation.components.userBottomNavItems

@Composable
fun ExploreScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: CreateAuctionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = androidx.compose.material3.SnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateAuctionEvent.Success -> snackbarHostState.showSnackbar("Subasta publicada")
                is CreateAuctionEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Gold,
        unfocusedBorderColor = GoldBorder,
        cursorColor = Gold,
        focusedTextColor = White90,
        unfocusedTextColor = White90,
        focusedLabelColor = Gold,
        unfocusedLabelColor = White50,
        errorBorderColor = RedLight
    )

    Scaffold(
        containerColor = Black2,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SyncBidBottomNav(
                items = userBottomNavItems,
                currentRoute = "create",
                onItemClick = onNavigateToRoute
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
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
                    text = "Nueva subasta",
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

            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── Image Upload Area ───
                Text(
                    text = "IMÁGENES DEL PRODUCTO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White30,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Black3)
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            tint = White30,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Arrastra o selecciona imágenes",
                            fontSize = 12.sp,
                            color = White50
                        )
                        Text(
                            text = ".JPG · .PNG · Max 5 MB",
                            fontSize = 10.sp,
                            color = White30
                        )
                    }
                }

                // ─── Product Name ───
                Text(
                    text = "NOMBRE DEL PRODUCTO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White30,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = { Text("iPhone 15 Pro Max 256GB", color = White30) },
                    isError = !uiState.isNameValid,
                    singleLine = true,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // ─── Description ───
                Text(
                    text = "DESCRIPCIÓN TÉCNICA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White30,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = { Text("A18 Pro · 48MP · Titanio natural…", color = White30) },
                    isError = !uiState.isDescValid,
                    minLines = 2,
                    maxLines = 3,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // ─── Base Price ───
                Text(
                    text = "PRECIO BASE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White30,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = uiState.basePrice,
                    onValueChange = viewModel::onBasePriceChange,
                    placeholder = { Text("500.00", color = White30) },
                    leadingIcon = {
                        Text(
                            text = "$",
                            color = White70,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    isError = !uiState.isPriceValid,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // ─── Duration ───
                Text(
                    text = "DURACIÓN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White30,
                    letterSpacing = 1.sp
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 30, 60).forEach { minutes ->
                        val selected = uiState.durationMinutes == minutes
                        val label = if (minutes < 60) "${minutes}m" else "${minutes / 60}h"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) GoldSubtle else Black3)
                                .border(
                                    1.dp,
                                    if (selected) Gold else GoldBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onDurationChange(minutes) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Gold else White50,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ─── Submit Button ───
                Button(
                    onClick = viewModel::submit,
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black,
                        disabledContainerColor = White.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "+ Publicar subasta",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
