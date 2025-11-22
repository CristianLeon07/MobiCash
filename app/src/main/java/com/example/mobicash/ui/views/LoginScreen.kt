package com.example.mobicash.ui.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobicash.data.biometric.BiometricAuthenticatorImpl
import com.example.mobicash.ui.navigation.HomeApp
import com.example.mobicash.ui.navigation.Login
import com.example.mobicash.ui.navigation.Register
import com.example.mobicash.ui.utils.UiState
import com.example.mobicash.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.TextFieldDefaults
import com.example.mobicash.ui.utils.PinInputBoxes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = remember(context) {
        context as? FragmentActivity
    }

    val biometricAuthenticator = remember(activity) {
        activity?.let { BiometricAuthenticatorImpl(it) }
    }

    // Código de LaunchedEffect y Biometría permanece sin cambios...

    LaunchedEffect(biometricAuthenticator) {
        biometricAuthenticator?.let {
            viewModel.checkBiometricStatus(it)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                navController.navigate(HomeApp) {
                    popUpTo(Login) { inclusive = true }
                }
            }

            is UiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                }
            }

            else -> Unit
        }
    }

    // Define el estilo de los campos de texto basado en RegisterScreen
    val defaultTextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp) // padding simétrico
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Icono / Logo
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = "MobiCash Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(48.dp))

            // TÍTULO (Opcional, para dar contexto)
            Text(
                "Bienvenido",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = viewModel.user,
                onValueChange = viewModel::onUserChange,
                label = { Text("Usuario") },
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                colors = defaultTextFieldColors,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(16.dp))

            // PIN (Implementación de 4 cajas)
            Text(
                text = "Ingresa tu PIN",
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


            Spacer(Modifier.height(32.dp))

            // 4. Botón de iniciar sesión (FUNCIONAL)
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                // enabled = viewModel.isLoginFormValid
            ) { Text("Iniciar sesión") }

            Spacer(Modifier.height(25.dp))

            // 6. TextButton sutil para Biometría (Flujo de Huella Preservado)
            if (viewModel.isBiometricAvailable) {
                TextButton(
                    onClick = {
                        biometricAuthenticator?.let { authenticator ->
                            viewModel.startBiometricAuth(
                                authenticator = authenticator,
                                onSuccess = {
                                    viewModel.biometricLogin()
                                    navController.navigate(HomeApp) {
                                        popUpTo(Login) { inclusive = true }
                                    }
                                },
                                onError = { msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            )
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Ingresa con tu huella", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(20.dp))

            // 5. Text ¿No tienes cuenta? REGÍSTRATE
            TextButton(onClick = { navController.navigate(Register) }) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color.Gray
                )
                Text(
                    text = "REGÍSTRATE",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}