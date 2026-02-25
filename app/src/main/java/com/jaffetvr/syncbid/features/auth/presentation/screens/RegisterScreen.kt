package com.jaffetvr.syncbid.features.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jaffetvr.syncbid.core.ui.theme.*
import com.jaffetvr.syncbid.features.auth.presentation.components.SyncBidTextField
import com.jaffetvr.syncbid.features.auth.presentation.viewModels.RegisterUiEvent
import com.jaffetvr.syncbid.features.auth.presentation.viewModels.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = SnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RegisterUiEvent.NavigateToDashboard -> onNavigateToDashboard()
                is RegisterUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Black2
    ) { padding ->
        // Box para centrar el contenido verticalmente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal de elementos
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // ─── Brand ───
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(GoldSubtle, RoundedCornerShape(16.dp))
                        .border(1.dp, GoldBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Únete a la comunidad de SyncBid",
                    style = MaterialTheme.typography.bodyLarge,
                    color = White30,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ─── Formulario ───
                SyncBidTextField(
                    value = uiState.fullName,
                    onValueChange = viewModel::onFullNameChange,
                    label = "Nombre completo",
                    leadingIcon = Icons.Outlined.Person,
                    isError = uiState.fullNameError != null,
                    isValid = uiState.isFullNameValid,
                    errorMessage = uiState.fullNameError,
                    placeholder = "Tu nombre completo"
                )

                Spacer(modifier = Modifier.height(16.dp))

                SyncBidTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Correo electrónico",
                    leadingIcon = Icons.Outlined.Email,
                    isError = uiState.emailError != null,
                    isValid = uiState.isEmailValid,
                    errorMessage = uiState.emailError,
                    placeholder = "correo@email.com"
                )

                Spacer(modifier = Modifier.height(16.dp))

                SyncBidTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Contraseña",
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError,
                    placeholder = "Mínimo 8 caracteres"
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ─── Botón Crear Cuenta ───
                Button(
                    onClick = viewModel::onRegisterClick,
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Black
                        )
                    } else {
                        Text(
                            text = "Crear cuenta",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ─── Link a login ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = White30
                    )
                    Text(
                        text = "Inicia sesión",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Gold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}