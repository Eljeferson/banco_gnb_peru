package com.example.appbanco_s8.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.appbanco_s8.navigation.Screen
import com.example.appbanco_s8.ui.theme.*
import kotlinx.coroutines.launch

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val isCentral: Boolean = false
)

data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    token: String,
    email: String,
    navController: NavHostController,
    onLogout: () -> Unit,
    onOpenDrawer: ((openFn: () -> Unit) -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    SideEffect {
        onOpenDrawer?.invoke {
            scope.launch { drawerState.open() }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/") ?: ""

    val nombreCorto = email.substringBefore("@").replaceFirstChar { it.uppercase() }
    val iniciales   = nombreCorto.take(2).uppercase()

    // --- Dynamic Dialog States for Drawer & Bottom Nav ---
    var showTokenDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }
    var showHelpImproveDialog by remember { mutableStateOf(false) }

    // --- Dynamic Token values simulation ---
    var tokenValue by remember { mutableStateOf(100000 + (Math.random() * 899999).toInt()) }
    var tokenProgress by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(showTokenDialog) {
        if (showTokenDialog) {
            while (true) {
                tokenProgress = 1f
                tokenValue = 100000 + (Math.random() * 899999).toInt()
                for (i in 1..150) {
                    kotlinx.coroutines.delay(200)
                    tokenProgress = (150 - i) / 150f
                }
            }
        }
    }

    val bottomItems = listOf(
        BottomNavItem("Inicio",   Icons.Filled.Home,          Screen.Home.route.substringBefore("/")),
        BottomNavItem("Opera",    Icons.Filled.SwapHoriz,     Screen.Opera.route.substringBefore("/")),
        BottomNavItem("Para mí",  Icons.Filled.CreditCard,    Screen.Tarjeta.route.substringBefore("/"), isCentral = true),
        BottomNavItem("Notifica", Icons.Filled.Notifications, Screen.Notifica.route.substringBefore("/")),
        BottomNavItem("Contacto", Icons.Filled.HeadsetMic,    Screen.Contacto.route)
    )

    val drawerItems = listOf(
        DrawerItem("Configuración",          Icons.Outlined.Settings,       { navController.navigate(Screen.Perfil.createRoute(token)) }),
        DrawerItem("Token Digital",          Icons.Outlined.Shield,         { showTokenDialog = true }),
        DrawerItem("Seguridad y privacidad", Icons.Outlined.Lock,           { navController.navigate(Screen.Perfil.createRoute(token)) }),
        DrawerItem("Operar con QR / Plin",   Icons.Outlined.QrCode,         { navController.navigate(Screen.Home.createRoute(token, email)) }),
        DrawerItem("Solicitar Préstamo",     Icons.Outlined.MonetizationOn, { navController.navigate(Screen.Prestamo.createRoute(token, email)) }),
        DrawerItem("Puntos y promociones",   Icons.Outlined.Stars,          { navController.navigate(Screen.Perfil.createRoute(token)) }),
        DrawerItem("Experiencias",           Icons.Outlined.Explore,        { navController.navigate(Screen.Perfil.createRoute(token)) }),
        DrawerItem("Historial retiro",       Icons.Outlined.History,        { navController.navigate(Screen.Cuenta.createRoute(token)) }),
        DrawerItem("Puntos de atención",     Icons.Outlined.LocationOn,     { navController.navigate(Screen.Contacto.route) }),
        DrawerItem("Zona de cobro",          Icons.Outlined.Calculate,      { navController.navigate(Screen.Contacto.route) }),
        DrawerItem("Aplicaciones",           Icons.Outlined.Apps,           { navController.navigate(Screen.Perfil.createRoute(token)) }),
        DrawerItem("Ayúdanos a mejorar",     Icons.Outlined.Favorite,       { showHelpImproveDialog = true }),
        DrawerItem("Acerca de Banco GNB",     Icons.Outlined.Info,           { showAppInfoDialog = true }),
        DrawerItem("Salir",                  Icons.Outlined.Logout,         onLogout, isDestructive = true)
    )

    // --- AlertDialogs Render ---
    if (showTokenDialog) {
        AlertDialog(
            onDismissRequest = { showTokenDialog = false },
            confirmButton = {
                Button(
                    onClick = { showTokenDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text(
                    text = "Token Digital GNB",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tu código de seguridad dinámico para confirmar transferencias, retiros y compras:",
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { tokenProgress },
                            modifier = Modifier.size(120.dp),
                            color = VerdeBotonForest,
                            strokeWidth = 6.dp,
                            trackColor = BordeSuave
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%06d".format(tokenValue),
                                color = VerdeBosqueOscuro,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Cambia en segundos",
                                color = GrisSage,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        )
    }

    if (showAppInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAppInfoDialog = false },
            confirmButton = {
                Button(
                    onClick = { showAppInfoDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueOscuro)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text("Acerca de Banco GNB", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("GNB Banca Móvil - Versión 3.5.2", color = VerdeBosqueOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Banco GNB Perú S.A. | Todos los derechos reservados 2026.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text("Esta aplicación móvil cuenta con los estándares más altos de encriptación y seguridad para proteger tu dinero y tus datos personales.", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
            }
        )
    }

    if (showHelpImproveDialog) {
        var feedbackText by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { 
                showHelpImproveDialog = false
                feedbackText = ""
                submitted = false
            },
            confirmButton = {
                Button(
                    onClick = { 
                        if (submitted) {
                            showHelpImproveDialog = false
                            feedbackText = ""
                            submitted = false
                        } else {
                            submitted = true
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(if (submitted) "Cerrar" else "Enviar sugerencia", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text("Ayúdanos a mejorar", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!submitted) {
                        Text("Tu opinión es muy valiosa para seguir optimizando la banca móvil de GNB.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            placeholder = { Text("Escribe tu comentario aquí...", color = GrisSage.copy(0.7f)) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedContainerColor = FondoCrema,
                                unfocusedContainerColor = FondoCrema,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🎉", fontSize = 48.sp)
                            Text("¡Muchas gracias por tus comentarios!", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, textAlign = TextAlign.Center)
                            Text("Nuestro equipo revisará tu sugerencia para seguir construyendo la mejor experiencia bancaria.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            DrawerContent(
                nombreCorto = nombreCorto,
                iniciales   = iniciales,
                items       = drawerItems,
                onClose     = { scope.launch { drawerState.close() } },
                onProfileClick = {
                    navController.navigate(Screen.Perfil.createRoute(token))
                }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavBar(
                    items        = bottomItems,
                    currentRoute = currentRoute,
                    onItemClick  = { item ->
                        val route = if (item.isCentral) {
                            Screen.Tarjeta.createRoute(token)
                        } else {
                            when (item.label) {
                                "Inicio"   -> Screen.Home.createRoute(token, email)
                                "Opera"    -> Screen.Opera.createRoute(token)
                                "Notifica" -> Screen.Notifica.createRoute(token)
                                "Contacto" -> Screen.Contacto.route
                                else       -> null
                            }
                        }
                        route?.let {
                            navController.navigate(it) {
                                popUpTo(Screen.Home.route.substringBefore("/")) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}


@Composable
private fun BottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier       = Modifier.height(68.dp)
            ) {
                items.forEach { item ->
                    val selected = currentRoute.startsWith(item.route.substringBefore("/"))
                    if (item.isCentral) {
                        NavigationBarItem(
                            selected = false,
                            onClick  = { onItemClick(item) },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(VerdeOscuro, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector        = item.icon,
                                        contentDescription = item.label,
                                        tint               = Color.White,
                                        modifier           = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label  = { Text(item.label, fontSize = 10.sp, color = GrisTextoSec, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        NavigationBarItem(
                            selected = selected,
                            onClick  = { onItemClick(item) },
                            icon = {
                                Icon(
                                    imageVector        = item.icon,
                                    contentDescription = item.label,
                                    modifier           = Modifier.size(22.dp)
                                )
                            },
                            label  = { Text(item.label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = VerdeOscuro,
                                selectedTextColor   = VerdeOscuro,
                                unselectedIconColor = GrisTextoSec,
                                unselectedTextColor = GrisTextoSec,
                                indicatorColor      = VerdeSage
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    nombreCorto: String,
    iniciales: String,
    items: List<DrawerItem>,
    onClose: () -> Unit,
    onProfileClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape          = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        modifier             = Modifier.width(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AzulOscuroGNB, AzulGNB)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(VerdeOscuro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = iniciales,
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text       = nombreCorto,
                    color      = Color.White,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = "Puntos GNB: 0",
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick  = { 
                        onProfileClick()
                        onClose()
                    },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Text(
                        text     = "Ver Perfil",
                        color    = VerdeGNB,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.onClick()
                        if (!item.isDestructive) onClose()
                    }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.label,
                    tint               = if (item.isDestructive) RojoError else AzulGNB,
                    modifier           = Modifier.size(20.dp)
                )
                Text(
                    text       = item.label,
                    color      = if (item.isDestructive) RojoError else MaterialTheme.colorScheme.onSurface,
                    fontSize   = 14.sp,
                    fontWeight = if (item.isDestructive) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (item.label == "Experiencias" || item.label == "Acerca de Banco GNB") {
                HorizontalDivider(
                    color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    thickness = 0.5.dp,
                    modifier  = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
