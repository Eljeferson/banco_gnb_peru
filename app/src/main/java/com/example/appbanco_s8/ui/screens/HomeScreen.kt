package com.example.appbanco_s8.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appbanco_s8.data.model.Cuenta
import com.example.appbanco_s8.data.model.Transaccion
import com.example.appbanco_s8.navigation.Screen
import com.example.appbanco_s8.ui.theme.*
import com.example.appbanco_s8.ui.viewmodel.DataUiState
import com.example.appbanco_s8.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    token: String,
    email: String,
    navController: NavHostController,
    onLogout: () -> Unit,
    onMenuClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val cuentasState       by viewModel.cuentas.collectAsStateWithLifecycle()
    val transaccionesState by viewModel.transacciones.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        viewModel.cargarDatos(token)
    }

    val nombreCorto = email.substringBefore("@")
        .replaceFirstChar { it.uppercase() }

    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            HeaderSection(
                nombreCorto = nombreCorto,
                onMenuClick = onMenuClick
            )
        }

        item {
            when (val state = transaccionesState) {
                is DataUiState.Success -> ResumenSection(
                    ingresos      = viewModel.ingresosMes,
                    gastos        = viewModel.gastosMes
                )
                is DataUiState.Loading -> LoadingCard()
                else -> Unit
            }
        }

        item { AccesosRapidosSection() }

        item { SectionTitle(title = "Mis Cuentas") }

        when (val state = cuentasState) {
            is DataUiState.Loading -> item { LoadingCard() }
            is DataUiState.Error   -> item { ErrorCard(mensaje = state.mensaje) }
            is DataUiState.Success -> {
                items(state.data) { cuenta ->
                    CuentaCard(
                        cuenta  = cuenta,
                        onClick = {
                            navController.navigate(Screen.Cuenta.createRoute(token))
                        }
                    )
                }
            }
        }

        item { SectionTitle(title = "Últimos movimientos") }

        when (val state = transaccionesState) {
            is DataUiState.Loading -> item { LoadingCard() }
            is DataUiState.Error   -> item { ErrorCard(mensaje = state.mensaje) }
            is DataUiState.Success -> {
                val ultimos = state.data.take(5)
                if (ultimos.isEmpty()) {
                    item {
                        Text(
                            text     = "Sin movimientos recientes",
                            color    = GrisTexto,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(ultimos) { tx ->
                        MovimientoRow(transaccion = tx)
                    }
                }
            }
        }

        item { AnalisisGastoCard() }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun HeaderSection(
    nombreCorto: String,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdeGNB)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text       = "Hola, $nombreCorto",
                    color      = Color.White,
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = "Bienvenido a Banco GNB",
                    color    = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            Row {
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.HelpOutline, "Ayuda", tint = Color.White)
                }
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, "Menú", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ResumenSection(ingresos: Double, gastos: Double) {
    val total = ingresos + gastos
    val progreso = if (total > 0) (ingresos / total).toFloat().coerceIn(0f, 1f) else 0.5f

    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = BlancoPuro,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Ingresos del mes", color = GrisTextoSec, fontSize = 13.sp)
                Text("S/ %,.2f".format(ingresos), color = VerdeOscuro, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = VerdeOscuro,
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Gastos del mes", color = GrisTextoSec, fontSize = 13.sp)
                Text("S/ %,.2f".format(gastos), color = RojoError, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AccesosRapidosSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AccesoItem(icon = Icons.Default.SwapHoriz, label = "Transferir")
        AccesoItem(icon = Icons.Default.QrCodeScanner, label = "QR")
        AccesoItem(icon = Icons.Default.Payments, label = "Pagar")
        AccesoItem(icon = Icons.Default.MoreHoriz, label = "Más")
    }
}

@Composable
private fun AccesoItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = AzulGNB.copy(alpha = 0.1f),
            onClick = { }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, label, tint = AzulGNB, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = AzulGNB, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AzulGNB,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun CuentaCard(cuenta: Cuenta, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = BlancoPuro,
        shadowElevation = 1.dp,
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column {
                Text(cuenta.tipo.uppercase(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                Text(cuenta.numeroCuenta, fontSize = 13.sp, color = GrisTextoSec)
            }
            Text("S/ %,.2f".format(cuenta.saldo), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = AzulOscuroGNB)
        }
    }
}

@Composable
private fun MovimientoRow(transaccion: Transaccion) {
    val esGasto = transaccion.esDebito()
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(40.dp), CircleShape, color = if (esGasto) RojoError.copy(0.12f) else VerdeOscuro.copy(0.12f)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    if (esGasto) Icons.Default.Remove else Icons.Default.Add,
                    null,
                    tint = if (esGasto) RojoError else VerdeOscuro
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(transaccion.descripcion, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = GrisTexto, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(transaccion.fecha.take(10), fontSize = 12.sp, color = GrisTextoSec)
        }
        Text(
            "${if (esGasto) "-" else "+"} S/ %,.2f".format(transaccion.monto),
            color = if (esGasto) RojoError else VerdeOscuro,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AnalisisGastoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = AzulGNB,
        onClick = { }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BarChart, null, tint = Color.White)
            Spacer(Modifier.width(12.dp))
            Text("Ver análisis de mis gastos", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.White)
        }
    }
}

@Composable
private fun LoadingCard() {
    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = VerdeGNB)
    }
}

@Composable
private fun ErrorCard(mensaje: String) {
    Text(mensaje, color = RojoError, modifier = Modifier.padding(16.dp))
}
