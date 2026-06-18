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
    primary          = VerdeBotonForest,       // Verde foresta sólido para botones y acentos
    onPrimary        = Color.White,
    primaryContainer = VerdeSage,
    onPrimaryContainer = VerdeBosqueOscuro,

    secondary        = AzulGNB,
    onSecondary      = Color.White,

    tertiary         = NaranjaGNB,
    onTertiary       = Color.White,

    background       = FondoCrema,
    onBackground     = VerdeBosqueOscuro,

    surface          = BlancoPuro,
    onSurface        = VerdeBosqueOscuro,
    surfaceVariant   = VerdeSage,
    onSurfaceVariant = GrisSage,

    outline          = BordeSuave,
    error            = RojoError,
    onError          = Color.White
)

@Composable
fun Appbanco_s8Theme(
    darkTheme: Boolean = false, // Siempre usar tema claro para la estética premium orgánica
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = FondoCrema.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
