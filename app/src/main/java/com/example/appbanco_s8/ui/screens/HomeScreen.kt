package com.example.appbanco_s8.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
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

    // --- Interactive Dialog States ---
    var showQrDialog by remember { mutableStateOf(false) }
    var showQrSuccessDialog by remember { mutableStateOf(false) }
    var showAnalyticsDialog by remember { mutableStateOf(false) }

    // --- QR Scanner Dialog ---
    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.padding(28.dp),
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Escáner QR GNB / PLIN",
                    color = VerdeBosqueOscuro,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Apunta la cámara al código QR para pagar o transferir al instante.",
                        color = GrisSage,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    // Views finder simulation
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Viewfinder borders
                            val borderSize = 30.dp.toPx()
                            val strokeWidth = 4.dp.toPx()
                            val pathEffect = PathEffect.cornerPathEffect(8.dp.toPx())

                            // Top-Left corner
                            drawArc(
                                color = VerdeBotonForest,
                                startAngle = 180f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                                size = androidx.compose.ui.geometry.Size(borderSize, borderSize)
                            )
                            // Top-Right corner
                            drawArc(
                                color = VerdeBotonForest,
                                startAngle = 270f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                                size = androidx.compose.ui.geometry.Size(borderSize, borderSize),
                                topLeft = androidx.compose.ui.geometry.Offset(size.width - borderSize, 0f)
                            )
                            // Bottom-Left corner
                            drawArc(
                                color = VerdeBotonForest,
                                startAngle = 90f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                                size = androidx.compose.ui.geometry.Size(borderSize, borderSize),
                                topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - borderSize)
                            )
                            // Bottom-Right corner
                            drawArc(
                                color = VerdeBotonForest,
                                startAngle = 0f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                                size = androidx.compose.ui.geometry.Size(borderSize, borderSize),
                                topLeft = androidx.compose.ui.geometry.Offset(size.width - borderSize, size.height - borderSize)
                            )
                        }

                        // Floating Scan line or scanning code
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = null,
                            tint = VerdeBosqueOscuro.copy(0.6f),
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Button(
                        onClick = {
                            showQrDialog = false
                            showQrSuccessDialog = true
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                    ) {
                        Text("Simular Escaneo Exitoso", fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    // --- QR Payment Confirmation Dialog ---
    if (showQrSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showQrSuccessDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showQrSuccessDialog = false
                        // Simulate local balance change or refresh data
                        viewModel.cargarDatos(token)
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Confirmar Pago", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrSuccessDialog = false }) {
                    Text("Rechazar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("Confirmar Pago QR", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Código QR leído de forma exitosa.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = VerdeSage,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text("Comercio:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Restaurante Organics S.A.", color = VerdeBosqueOscuro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text("Monto:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("S/ 45.00", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        )
    }

    // --- Analytics Dialog ---
    if (showAnalyticsDialog) {
        AlertDialog(
            onDismissRequest = { showAnalyticsDialog = false },
            confirmButton = {
                Button(
                    onClick = { showAnalyticsDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Análisis de mis Gastos",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Aquí tienes el desglose visual de tus egresos durante este mes.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    // Simulated breakdown bars
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AnalyticsBar(category = "Alimentos & Supermercado", amount = 350.00, percentage = 0.58f, color = VerdeBotonForest)
                        AnalyticsBar(category = "Transporte & Gasolina", amount = 120.00, percentage = 0.20f, color = VerdeBosqueOscuro)
                        AnalyticsBar(category = "Servicios Hogar", amount = 85.00, percentage = 0.14f, color = AmarilloGNB)
                        AnalyticsBar(category = "Otros Gastos", amount = 48.00, percentage = 0.08f, color = NaranjaGNB)
                    }
                }
            }
        )
    }

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

        item {
            AccesosRapidosSection(
                onTransferir = { navController.navigate(Screen.Opera.createRoute(token)) },
                onQr = { showQrDialog = true },
                onPagar = { navController.navigate(Screen.Opera.createRoute(token)) },
                onPrestamos = { navController.navigate(Screen.Prestamo.createRoute(token, email)) }
            )
        }

        item { SectionTitle(title = "Mis Cuentas", onVerMas = { navController.navigate(Screen.Cuenta.createRoute(token)) }) }

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

        item { SectionTitle(title = "Últimos movimientos", onVerMas = { navController.navigate(Screen.Cuenta.createRoute(token)) }) }

        when (val state = transaccionesState) {
            is DataUiState.Loading -> item { LoadingCard() }
            is DataUiState.Error   -> item { ErrorCard(mensaje = state.mensaje) }
            is DataUiState.Success -> {
                val ultimos = state.data.take(5)
                if (ultimos.isEmpty()) {
                    item {
                        Text(
                            text     = "Sin movimientos recientes",
                            color    = GrisSage,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = BlancoPuro,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                ultimos.forEachIndexed { index, tx ->
                                    MovimientoRow(transaccion = tx)
                                    if (index < ultimos.lastIndex) {
                                        HorizontalDivider(
                                            color = BordeSuave,
                                            thickness = 0.5.dp,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            AnalisisGastoCard(
                onClick = { showAnalyticsDialog = true }
            )
        }
        item { Spacer(Modifier.height(112.dp)) } // Spacing above the floating bottom bar
    }
}

@Composable
private fun HeaderSection(
    nombreCorto: String,
    onMenuClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = BlancoPuro,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                    onClick = onMenuClick
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Menu, "Menú", tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                    }
                }
                Column {
                    Text(
                        text = "Hola, $nombreCorto",
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gestiona tus cuentas",
                        color = VerdeBosqueOscuro,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = BlancoPuro,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                    onClick = {}
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.HelpOutline, "Ayuda", tint = VerdeBosqueOscuro, modifier = Modifier.size(18.dp))
                    }
                }
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = VerdeSage,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = nombreCorto.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = VerdeBosqueOscuro,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(50.dp),
                color = VerdeSage,
                onClick = {}
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Search, null, tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Buscar operaciones, cuentas...",
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = VerdeBotonForest,
                onClick = {}
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.StarBorder, null, tint = Color.White, modifier = Modifier.size(20.dp))
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = BlancoPuro,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Ingresos del mes", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("S/ %,.2f".format(ingresos), color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = VerdeBotonForest,
                trackColor = BordeSuave
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("Gastos del mes", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("S/ %,.2f".format(gastos), color = RojoError, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun AccesosRapidosSection(
    onTransferir: () -> Unit,
    onQr: () -> Unit,
    onPagar: () -> Unit,
    onPrestamos: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AccesoItem(icon = Icons.Default.SwapHoriz, label = "Transferir", onClick = onTransferir)
        AccesoItem(icon = Icons.Default.QrCodeScanner, label = "QR", onClick = onQr)
        AccesoItem(icon = Icons.Default.Payments, label = "Pagar", onClick = onPagar)
        AccesoItem(icon = Icons.Default.MonetizationOn, label = "Préstamos", onClick = onPrestamos)
    }
}

@Composable
private fun AccesoItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = VerdeSage,
            border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, label, tint = VerdeBosqueOscuro, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun SectionTitle(title: String, onVerMas: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = VerdeBosqueOscuro,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Ver más",
            color = VerdeBotonForest,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.clickable { onVerMas() }
        )
    }
}

@Composable
private fun CuentaCard(cuenta: Cuenta, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        color = BlancoPuro,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
        onClick = onClick
    ) {
        Row(Modifier.padding(20.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column {
                Text(cuenta.tipo.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = VerdeBosqueOscuro, letterSpacing = 0.5.sp)
                Text(cuenta.numeroCuenta, fontSize = 12.sp, color = GrisSage, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Bold)
            }
            Text("S/ %,.2f".format(cuenta.saldo), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = VerdeBosqueOscuro)
        }
    }
}

@Composable
private fun MovimientoRow(transaccion: Transaccion) {
    val esGasto = transaccion.esDebito()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = if (esGasto) RojoError.copy(0.08f) else VerdeSage
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    if (esGasto) Icons.Default.Remove else Icons.Default.Add,
                    null,
                    tint = if (esGasto) RojoError else VerdeBotonForest,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = transaccion.descripcion,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VerdeBosqueOscuro,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaccion.fecha.take(10),
                fontSize = 12.sp,
                color = GrisSage,
                modifier = Modifier.padding(top = 2.dp),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "${if (esGasto) "-" else "+"} S/ %,.2f".format(transaccion.monto),
            color = if (esGasto) RojoError else VerdeBotonForest,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun AnalisisGastoCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = VerdeSage,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
        onClick = onClick
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = BlancoPuro
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.BarChart, null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Text("Ver análisis de mis gastos", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun AnalyticsBar(category: String, amount: Double, percentage: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(category, color = VerdeBosqueOscuro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("S/ %,.2f".format(amount), color = VerdeBosqueOscuro, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = BordeSuave
        )
    }
}

@Composable
private fun LoadingCard() {
    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = VerdeBotonForest)
    }
}

@Composable
private fun ErrorCard(mensaje: String) {
    Text(mensaje, color = RojoError, modifier = Modifier.padding(16.dp))
}
