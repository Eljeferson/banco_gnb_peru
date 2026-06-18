
package com.example.appbanco_s8.navigation

sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Home      : Screen("home/{token}/{email}") {
        fun createRoute(token: String, email: String) = "home/$token/$email"
    }
    object Cuenta    : Screen("cuenta/{token}") {
        fun createRoute(token: String) = "cuenta/$token"
    }
    object Tarjeta   : Screen("tarjeta/{token}") {
        fun createRoute(token: String) = "tarjeta/$token"
    }
    object Prestamo  : Screen("prestamo/{token}/{email}") {
        fun createRoute(token: String, email: String) = "prestamo/$token/$email"
    }
    object Opera     : Screen("opera/{token}") {
        fun createRoute(token: String) = "opera/$token"
    }
    object Notifica  : Screen("notifica/{token}") {
        fun createRoute(token: String) = "notifica/$token"
    }
    object Contacto  : Screen("contacto")
    object Perfil    : Screen("perfil/{token}") {
        fun createRoute(token: String) = "perfil/$token"
    }
}