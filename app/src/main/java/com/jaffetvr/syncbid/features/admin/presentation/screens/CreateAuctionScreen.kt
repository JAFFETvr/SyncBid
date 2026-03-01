package com.jaffetvr.syncbid.features.admin.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.jaffetvr.syncbid.features.admin.presentation.components.AdminBottomNav
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.CreateAuctionEvent
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.CreateAuctionViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAuctionScreen(
    onNavigateToRoute: (String) -> Unit,
    viewModel: CreateAuctionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) viewModel.onImageChange(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateAuctionEvent.Success -> {
                    snackbarHostState.showSnackbar("Subasta creada exitosamente")
                    onNavigateToRoute("admin_inventory")
                }
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
            AdminBottomNav(
                currentRoute = "admin_create",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nueva Subasta",
                style = MaterialTheme.typography.headlineMedium,
                color = White,
                fontWeight = FontWeight.Bold
            )

            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Black3)
                    .border(1.dp, if (uiState.imageUri != null) Gold else GoldBorder, RoundedCornerShape(12.dp))
                    .clickable {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUri != null) {
                    Text("Imagen seleccionada ✓", color = Gold, fontSize = 16.sp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+", color = Gold, fontSize = 32.sp)
                        Text("Añadir imagen", color = White70, fontSize = 14.sp)
                    }
                }
            }

            // Nombre
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre del artículo") },
                isError = !uiState.isNameValid,
                singleLine = true,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Descripción
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción") },
                isError = !uiState.isDescValid,
                minLines = 3,
                maxLines = 5,
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Precio
            OutlinedTextField(
                value = uiState.basePrice,
                onValueChange = viewModel::onBasePriceChange,
                label = { Text("Precio inicial (USD)") },
                isError = !uiState.isPriceValid,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Duración (Chips)
            Text(
                text = "DURACIÓN",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = White30,
                letterSpacing = 1.sp
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.durationOptions.forEach { minutes ->
                    val selected = uiState.durationMinutes == minutes
                    val label = if (minutes < 60) "${minutes}m" else "${minutes / 60}h"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) GoldSubtle else Black3)
                            .border(1.dp, if (selected) Gold else GoldBorder, RoundedCornerShape(20.dp))
                            .clickable { viewModel.onDurationChange(minutes) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(label, color = if (selected) Gold else White70, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::submit,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Black, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                } else {
                    Text("Publicar Subasta", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}