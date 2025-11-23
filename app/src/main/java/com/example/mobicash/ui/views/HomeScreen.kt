package com.example.mobicash.ui.views


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.mobicash.domain.models.UserModel
import com.example.mobicash.ui.navigation.HomeApp
import com.example.mobicash.ui.navigation.Login
import com.example.mobicash.ui.utils.UiState
import com.example.mobicash.ui.viewmodels.HomeViewModel
import com.example.mobicash.ui.viewmodels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val cardInfo by viewModel.cardInfo.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Control de inactividad 60s
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (System.currentTimeMillis() - lastInteractionTime > 60_000) {
                scope.launch { drawerState.close() }
                navController.navigate(Login) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val interactionModifier = Modifier.pointerInput(Unit) {
        while (true) {
            awaitPointerEventScope { awaitPointerEvent() }
            lastInteractionTime = System.currentTimeMillis()
        }
    }

    // ---------------------------------------------------------------
    // DRAWER
    // ---------------------------------------------------------------
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 36.dp, bottom = 12.dp)
                ) {
                    Text(
                        "MobiCash",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Menú principal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("profile")
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    icon = { Icon(Icons.Default.ExitToApp, null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = interactionModifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "MobiCash",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {

                // -------------------------------------------------------
                // ESTADO PRINCIPAL
                // -------------------------------------------------------
                when (uiState) {

                    is UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Error -> {
                        Text(
                            "Ocurrió un error al cargar información",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    is UiState.Success -> {
                        val user = (uiState as UiState.Success<UserModel>).data

                        // ----------------------
                        // Saludo
                        // ----------------------
                        Text(
                            text = "Hola, ${user.name}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Bienvenido a tu banca digital.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(20.dp))


                        //  TARJETA BANCARIA
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(24.dp)
                            ) {

                                Column {
                                    Text(
                                        text = "Cuenta de ahorros",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Text(
                                        text = cardInfo?.maskedCardNumber ?: "**** **** **** ****",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = "$${cardInfo?.balance ?: "0"}",
                                        style = MaterialTheme.typography.displayLarge,
                                        color = Color.White
                                    )
                                }

                                // círculos decorativos
                                Canvas(
                                    modifier = Modifier
                                        .size(160.dp)
                                        .align(Alignment.BottomEnd)
                                        .alpha(0.15f)
                                ) {
                                    drawCircle(Color.White, radius = 200f)
                                }
                            }
                        }

                        Spacer(Modifier.height(28.dp))

                        // ======================================================
                        // SECCIÓN DE ACCIONES
                        // ======================================================
                        Text(
                            "Acciones rápidas",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            QuickAction(icon = Icons.Default.Send, title = "Enviar")
                            QuickAction(icon = Icons.Default.AccountBalance, title = "Recargar")
                            QuickAction(icon = Icons.Default.Receipt, title = "Pagos")
                        }

                        Spacer(Modifier.height(30.dp))

                        // ======================================================
                        // MOVIMIENTOS
                        // ======================================================
                        Text(
                            "Movimientos recientes",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text(
                                    "Aún no tienes movimientos.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    UiState.Idle -> {}
                }
            }
        }
    }
}


// ---------------------------------------------------
// COMPONENTE ACCIÓN RÁPIDA
// ---------------------------------------------------
@Composable
fun QuickAction(
    icon: ImageVector,
    title: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

