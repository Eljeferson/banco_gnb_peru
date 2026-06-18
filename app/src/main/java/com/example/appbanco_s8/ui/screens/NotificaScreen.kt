package com.example.appbanco_s8.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appbanco_s8.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificaScreen(token: String, navController: NavHostController) {
    // --- Interactive States for Alerts Dialog ---
    var showNotifDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }

    // Dialog: List of alerts inside selected category
    if (showNotifDialog) {
        val alertsList = when (selectedCategory) {
            "Compras con tarjeta" -> listOf(
                Pair("Compra aprobada: S/ 45.00 en Restaurante Organics S.A.", "Hoy, 09:12 a.m."),
                Pair("Compra aprobada: S/ 12.00 en Gasolinera Primax", "Ayer, 06:34 p.m."),
                Pair("Compra aprobada: S/ 115.00 en Supermercados Wong", "30 de Mayo, 03:45 p.m."),
                Pair("Compra aprobada: S/ 25.00 en Cineplanet Prime", "28 de Mayo, 08:20 p.m.")
            )
            "Avisos y recordatorios" -> listOf(
                Pair("Tu pago de Tarjeta de Crédito GNB vence en 3 días (Monto: S/ 450.00).", "Hace 2 horas"),
                Pair("Tu estado de cuenta correspondiente al mes de Mayo ya está disponible para descarga.", "Ayer, 09:00 a.m."),
                Pair("Nueva actualización de seguridad exitosa en tu Token Digital.", "29 de Mayo, 11:15 a.m."),
                Pair("Tu préstamo simulado ha sido pre-aprobado. Revisa tu simulador de préstamos GNB.", "26 de Mayo, 04:30 p.m.")
            )
            else -> listOf(
                Pair("🌳 ¡Campaña Bosque Verde! Duplica tus Puntos GNB hoy consumiendo en comercios ecológicos.", "Válido hoy"),
                Pair("50% de descuento en Almuerzos Ejecutivos los miércoles pagando con GNB Platinum.", "Válido mié."),
                Pair("Pre-aprobación exclusiva de Préstamo Personal GNB con Tasa de Interés del 11.5% E.A.", "Vence en 5 días"),
                Pair("Campaña de compras internacionales sin cobro de comisión por tipo de cambio.", "Vence en 10 días")
            )
        }

        AlertDialog(
            onDismissRequest = { showNotifDialog = false },
            confirmButton = {
                Button(
                    onClick = { showNotifDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text(selectedCategory, color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    alertsList.forEach { alert ->
                        Surface(
                            color = FondoCrema,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = alert.first,
                                    color = VerdeBosqueOscuro,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 18.sp
                                )
                                Text(
                                    text = alert.second,
                                    color = GrisSage,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    data class NotifItem(val icon: ImageVector, val label: String, val sublabel: String = "")

    val notificaciones = listOf(
        NotifItem(Icons.Outlined.CreditCard,    "Compras con tarjeta",     "Notificaciones"),
        NotifItem(Icons.Default.Info,           "Avisos y recordatorios",  "Notificaciones")
    )
    val oportunidades = listOf(
        NotifItem(Icons.Outlined.LocalOffer,    "Ofertas y promociones",   "Oportunidades")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 112.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Centro de Mensajes",
                    color      = VerdeBosqueOscuro,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Section Title: NOTIFICACIONES
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = VerdeSage,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                ) {
                    Text(
                        text     = "NOTIFICACIONES",
                        color    = VerdeBosqueOscuro,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Notificaciones Card Container
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column {
                    notificaciones.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedCategory = item.label
                                    showNotifDialog = true
                                }
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(42.dp),
                                shape = CircleShape,
                                color = VerdeSage,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(item.icon, contentDescription = null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                                }
                            }
                            Text(item.label, color = VerdeBosqueOscuro, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                        }
                        if (index < notificaciones.lastIndex) {
                            HorizontalDivider(
                                color = BordeSuave,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section Title: OPORTUNIDADES
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = VerdeSage,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                ) {
                    Text(
                        text     = "OPORTUNIDADES",
                        color    = VerdeBosqueOscuro,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Oportunidades Card Container
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column {
                    oportunidades.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedCategory = item.label
                                    showNotifDialog = true
                                }
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(42.dp),
                                shape = CircleShape,
                                color = VerdeSage,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(item.icon, contentDescription = null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                                }
                            }
                            Text(item.label, color = VerdeBosqueOscuro, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                        }
                        if (index < oportunidades.lastIndex) {
                            HorizontalDivider(
                                color = BordeSuave,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
