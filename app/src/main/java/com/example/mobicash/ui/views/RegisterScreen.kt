package com.example.mobicash.ui.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobicash.ui.navigation.Login
import com.example.mobicash.ui.navigation.Register
import com.example.mobicash.ui.utils.PinInputBoxes
import com.example.mobicash.ui.utils.UiState
import com.example.mobicash.ui.viewmodels.RegisterViewModel
import com.example.mobicash.utils.AnimatedError


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observa UiState para navegación y errores
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar(
                    "Registro exitoso",
                    duration = SnackbarDuration.Short
                )

                navController.navigate(Login) {
                    popUpTo(Register) { inclusive = true }
                }
            }

            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background // Fondo del tema
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            RegisterContent(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Register) { inclusive = true }
                    }
                }
            )

            if (uiState is UiState.Loading) {
                LoadingOverlay()
            }
        }
    }
}


// 2. CONTENIDO DE LA PANTALLA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterContent(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    val defaultTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorBorderColor = MaterialTheme.colorScheme.error,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor = MaterialTheme.colorScheme.error
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        // TÍTULOS
        Text(
            "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Únete a Mobicash",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // CAMPOS DE TEXTO

        // USUARIO
        OutlinedTextField(
            value = viewModel.user,
            onValueChange = viewModel::onUserChange,
            label = { Text("Usuario") },
            leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            isError = viewModel.userError != null,
            colors = defaultTextFieldColors,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        AnimatedError(viewModel.userError)

        Spacer(modifier = Modifier.height(16.dp))

        // NOMBRE
        OutlinedTextField(
            value = viewModel.name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Nombre completo") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = viewModel.nameError != null,
            colors = defaultTextFieldColors,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        AnimatedError(viewModel.nameError)

        Spacer(modifier = Modifier.height(16.dp))

        // EMAIL
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            isError = viewModel.emailError != null,
            colors = defaultTextFieldColors,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        AnimatedError(viewModel.emailError)

        Spacer(modifier = Modifier.height(16.dp))

        // PIN (Implementación de 4 cajas)
        Text(
            text = "PIN (4 dígitos)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start) // Etiqueta separada
        )
        Spacer(modifier = Modifier.height(8.dp))

        PinInputBoxes(
            pinValue = viewModel.pin,
            onPinChange = viewModel::onPinChange,
            isError = viewModel.pinError != null,
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedError(viewModel.pinError)

        Spacer(modifier = Modifier.height(32.dp))

        // BOTÓN REGISTRAR
        Button(
            onClick = { viewModel.registerUser() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Registrarme")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN INICIAR SESIÓN
        TextButton(
            onClick = onNavigateToLogin,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "¿Ya tienes cuenta? Inicia sesión",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}


@Composable
private fun LoadingOverlay() {
    // ... (Tu código actual de LoadingOverlay)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}
