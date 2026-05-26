package com.example.appbanco_s8.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appbanco_s8.ui.components.AppScaffold
import com.example.appbanco_s8.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {

    var tokenGlobal by remember { mutableStateOf("") }
    var emailGlobal by remember { mutableStateOf("") }

    // ── Lambda de logout reutilizable ────────────────────────
    val doLogout: () -> Unit = {
        tokenGlobal = ""
        emailGlobal = ""
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {

        // ── Login — sin scaffold ─────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { token, email ->
                    tokenGlobal = token
                    emailGlobal = email
                    navController.navigate(Screen.Home.createRoute(token, email)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ─────────────────────────────────────────────────────
        composable(
            route     = Screen.Home.route,
            arguments = listOf(
                navArgument("token") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            val email = back.arguments?.getString("email") ?: emailGlobal
            if (token.isNotEmpty()) {
                tokenGlobal = token
                emailGlobal = email
            }

            // openDrawer guarda la función que AppScaffold expone
            var openDrawer by remember { mutableStateOf<(() -> Unit)?>(null) }

            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout,
                onOpenDrawer  = { fn -> openDrawer = fn }   // ← recibe la fn del drawer
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    HomeScreen(
                        token         = tokenGlobal,
                        email         = emailGlobal,
                        navController = navController,
                        onLogout      = doLogout,
                        onMenuClick   = { openDrawer?.invoke() }  // ← llama sin scope
                    )
                }
            }
        }

        // ── Cuenta ───────────────────────────────────────────
        composable(
            route     = Screen.Cuenta.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    CuentaScreen(token = token, navController = navController)
                }
            }
        }

        // ── Tarjeta ──────────────────────────────────────────
        composable(
            route     = Screen.Tarjeta.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    TarjetaScreen(token = token, navController = navController)
                }
            }
        }

        // ── Prestamo ─────────────────────────────────────────
        composable(
            route     = Screen.Prestamo.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    PrestamoScreen(token = token, navController = navController)
                }
            }
        }

        // ── Opera ────────────────────────────────────────────
        composable(
            route     = Screen.Opera.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    OperaScreen(token = token, navController = navController)
                }
            }
        }

        // ── Notifica ─────────────────────────────────────────
        composable(
            route     = Screen.Notifica.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    NotificaScreen(token = token, navController = navController)
                }
            }
        }

        // ── Contacto ─────────────────────────────────────────
        composable(Screen.Contacto.route) {
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    ContactoScreen(navController = navController)
                }
            }
        }

        // ── Perfil ───────────────────────────────────────────
        composable(
            route     = Screen.Perfil.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments?.getString("token") ?: tokenGlobal
            AppScaffold(
                token         = tokenGlobal,
                email         = emailGlobal,
                navController = navController,
                onLogout      = doLogout
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    PerfilScreen(token = token, navController = navController)
                }
            }
        }
    }
}