package com.example.appbanco_s8.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary          = VerdeGNB,
    onPrimary        = Color.Black,
    primaryContainer = VerdeOscuro,
    onPrimaryContainer = Color.White,

    secondary        = AzulClaroGNB,
    onSecondary      = Color.Black,

    tertiary         = NaranjaClaro,
    onTertiary       = Color.Black,
    
    background       = Color(0xFF0B121F),      // Azul oscuro profundo y premium
    onBackground     = Color(0xFFE2E8F0),      // Gris/blanco de alto contraste
    
    surface          = Color(0xFF162235),      // Superficie oscura de tarjetas
    onSurface        = Color(0xFFFFFFFF),      // Texto blanco en tarjetas
    surfaceVariant   = Color(0xFF1F2F48),
    onSurfaceVariant = Color(0xFF94A3B8),
    
    outline          = Color(0xFF334155),
    error            = RojoError,
    onError          = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary          = VerdeOscuro,            // Verde más oscuro para mejor contraste en fondo claro
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFE2F3D1),
    onPrimaryContainer = VerdeOscuro,

    secondary        = AzulGNB,
    onSecondary      = Color.White,

    tertiary         = NaranjaGNB,
    onTertiary       = Color.White,

    background       = GrisFondo,
    onBackground     = GrisTexto,

    surface          = BlancoPuro,
    onSurface        = GrisTexto,
    surfaceVariant   = GrisFondoClaro,
    onSurfaceVariant = GrisTextoSec,

    outline          = Color(0xFFD1D1D1),
    error            = RojoError,
    onError          = Color.White
)

@Composable
fun Appbanco_s8Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) Color(0xFF0B121F).toArgb() else VerdeOscuro.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
