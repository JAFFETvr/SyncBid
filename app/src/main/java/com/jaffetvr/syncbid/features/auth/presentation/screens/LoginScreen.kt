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
import androidx.compose.material.icons.outlined.TrendingUp
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
import com.jaffetvr.syncbid.features.auth.presentation.components.SyncBidDivider
import com.jaffetvr.syncbid.features.auth.presentation.components.SyncBidTextField
import com.jaffetvr.syncbid.features.auth.presentation.viewModels.LoginUiEvent
import com.jaffetvr.syncbid.features.auth.presentation.viewModels.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = SnackbarHostState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginUiEvent.NavigateToDashboard -> onNavigateToDashboard()
                is LoginUiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Black2
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // ─── Brand ───
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(GoldSubtle, RoundedCornerShape(14.dp))
                    .border(1.dp, GoldBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineLarge,
                color = White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.bodyLarge,
                color = White30
            )
            Spacer(modifier = Modifier.height(36.dp))

            // ─── Correo electrónico ───
            SyncBidTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = "Correo electrónico",
                leadingIcon = Icons.Outlined.Email,
                isError = uiState.emailError != null,
                isValid = uiState.email.contains("@") && uiState.emailError == null && uiState.email.isNotEmpty(),
                errorMessage = uiState.emailError,
                placeholder = "usuario@email.com"
            )
            Spacer(modifier = Modifier.height(14.dp))

            // ─── Contraseña ───
            SyncBidTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                placeholder = "••••••••"
            )
            Spacer(modifier = Modifier.height(18.dp))

            // ─── Botón Continuar ───
            Button(
                onClick = viewModel::onLoginClick,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Black
                    )
                } else {
                    Text(
                        text = "Continuar",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            SyncBidDivider(text = "o")

            // ─── Botón Google ───
            OutlinedButton(
                onClick = { /* Google sign-in */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = White60),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(White12)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = "Continuar con Google",
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ─── Link a registro ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White30
                )
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Gold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
