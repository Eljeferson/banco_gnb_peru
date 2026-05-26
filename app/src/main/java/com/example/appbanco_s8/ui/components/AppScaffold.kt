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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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

    val bottomItems = listOf(
        BottomNavItem("Inicio",   Icons.Filled.Home,          Screen.Home.route.substringBefore("/")),
        BottomNavItem("Opera",    Icons.Filled.SwapHoriz,     Screen.Opera.route.substringBefore("/")),
        BottomNavItem("Para mí",  Icons.Filled.Add,           Screen.Cuenta.route.substringBefore("/"), isCentral = true),
        BottomNavItem("Notifica", Icons.Filled.Notifications, Screen.Notifica.route.substringBefore("/")),
        BottomNavItem("Contacto", Icons.Filled.HeadsetMic,    Screen.Contacto.route)
    )

    val drawerItems = listOf(
        DrawerItem("Configuración",          Icons.Outlined.Settings,       {}),
        DrawerItem("Token Digital",          Icons.Outlined.Shield,         {}),
        DrawerItem("Seguridad y privacidad", Icons.Outlined.Lock,           {}),
        DrawerItem("Operativas",             Icons.Outlined.AccountBalance, {}),
        DrawerItem("Operar con QR / Plin",   Icons.Outlined.QrCode,         {}),
        DrawerItem("Puntos y promociones",   Icons.Outlined.Stars,          {}),
        DrawerItem("Experiencias",           Icons.Outlined.Explore,        {}),
        DrawerItem("Historial retiro",       Icons.Outlined.History,        {}),
        DrawerItem("Puntos de atención",     Icons.Outlined.LocationOn,     {}),
        DrawerItem("Zona de cobro",          Icons.Outlined.Calculate,      {}),
        DrawerItem("Aplicaciones",           Icons.Outlined.Apps,           {}),
        DrawerItem("Ayúdanos a mejorar",     Icons.Outlined.Favorite,       {}),
        DrawerItem("Acerca de Banco GNB",     Icons.Outlined.Info,           {}),
        DrawerItem("Salir",                  Icons.Outlined.Logout,         onLogout, isDestructive = true)
    )

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            DrawerContent(
                nombreCorto = nombreCorto,
                iniciales   = iniciales,
                items       = drawerItems,
                onClose     = { scope.launch { drawerState.close() } }
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
                        if (!item.isCentral) {
                            val route = when (item.label) {
                                "Inicio"   -> Screen.Home.createRoute(token, email)
                                "Opera"    -> Screen.Opera.createRoute(token)
                                "Notifica" -> Screen.Notifica.createRoute(token)
                                "Contacto" -> Screen.Contacto.route
                                else       -> null
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
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier       = Modifier.height(64.dp)
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
                                .size(46.dp)
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
                    label  = { Text(item.label, fontSize = 10.sp, color = GrisTextoSec) },
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
                    label  = { Text(item.label, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = VerdeOscuro,
                        selectedTextColor   = VerdeOscuro,
                        unselectedIconColor = GrisTextoSec,
                        unselectedTextColor = GrisTextoSec,
                        indicatorColor      = VerdeGNB.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}

@Composable
private fun DrawerContent(
    nombreCorto: String,
    iniciales: String,
    items: List<DrawerItem>,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier             = Modifier.width(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AzulOscuroGNB, AzulGNB)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(52.dp)
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
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = nombreCorto,
                    color      = Color.White,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = "Puntos GNB: 0",
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick  = { onClose() },
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
