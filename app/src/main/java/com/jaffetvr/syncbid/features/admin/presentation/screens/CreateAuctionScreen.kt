package com.jaffetvr.syncbid.features.admin.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
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
import com.jaffetvr.syncbid.core.ui.theme.RedLight
import com.jaffetvr.syncbid.core.ui.theme.White50
import com.jaffetvr.syncbid.core.ui.theme.White70
import com.jaffetvr.syncbid.core.ui.theme.White90
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.CreateAuctionEvent
import com.jaffetvr.syncbid.features.admin.presentation.viewModels.CreateAuctionViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateAuctionScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateAuctionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImageChange(uri)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateAuctionEvent.Success -> onSuccess()
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
        containerColor = Black,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Nueva Subasta", color = White90, fontSize = 17.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Área de Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Black3)
                    .border(1.dp, if (uiState.imageUri != null) Gold else GoldBorder, RoundedCornerShape(12.dp))
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageUri != null) {
                    Text(text = "Imagen seleccionada ✓", color = Gold, fontSize = 16.sp)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "+", color = Gold, fontSize = 32.sp)
                        Text(text = "Añadir imagen", color = White70, fontSize = 14.sp)
                    }
                }
            }

            // Nombre / Título
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

            // Duración
            Text("Duración de la subasta", color = White70, fontSize = 13.sp)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.durationOptions.forEach { hours ->
                    val selected = uiState.durationHours == hours
                    val label = if (hours < 24) "${hours}h" else "${hours / 24}d"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) GoldSubtle else Black3)
                            .border(1.dp, if (selected) Gold else GoldBorder, RoundedCornerShape(20.dp))
                            .clickable { viewModel.onDurationChange(hours) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(text = label, color = if (selected) Gold else White70, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Publicar
            Button(
                onClick = viewModel::submit,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Black,
                    disabledContainerColor = Gold.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Black, strokeWidth = 2.dp, modifier = Modifier.padding(4.dp))
                } else {
                    Text("Publicar Subasta", fontSize = 15.sp)
                }
            }
        }
    }
}