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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
import com.example.appbanco_s8.ui.viewmodel.CuentaViewModel
import com.example.appbanco_s8.ui.viewmodel.DataUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaScreen(
    token: String,
    navController: NavHostController,
    viewModel: CuentaViewModel = viewModel()
) {
    val cuentasState by viewModel.cuentas.collectAsStateWithLifecycle()
    val transaccionesState by viewModel.transacciones.collectAsStateWithLifecycle()
    val ahorroState by viewModel.ahorro.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        viewModel.cargarDatos(token)
    }

    // --- Tab Selection ---
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Corriente, 1 = Ahorro

    // --- Interactive States (Local balances for transfer simulations) ---
    var hasInitializedLocalBalances by remember { mutableStateOf(false) }
    var localCorrienteBalance by remember { mutableDoubleStateOf(3450.0) }
    var localAhorroBalance by remember { mutableDoubleStateOf(1200.0) }
    var localAhorroMeta by remember { mutableDoubleStateOf(5000.0) }

    if (!hasInitializedLocalBalances) {
        if (cuentasState is DataUiState.Success && ahorroState is DataUiState.Success) {
            val cuentasList = (cuentasState as DataUiState.Success<List<Cuenta>>).data
            val corriente = cuentasList.firstOrNull { it.tipo == "corriente" }
            if (corriente != null) {
                localCorrienteBalance = corriente.saldo
            }
            val ahorroObj = (ahorroState as DataUiState.Success).data
            if (ahorroObj != null) {
                localAhorroBalance = ahorroObj.saldo
                localAhorroMeta = ahorroObj.metaAhorro
            }
            hasInitializedLocalBalances = true
        }
    }

    // --- Search filter for Transactions ---
    var searchQuery by remember { mutableStateOf("") }

    // --- Interactive Dialogs ---
    var showCopyToast by remember { mutableStateOf(false) }
    var showAbonoDialog by remember { mutableStateOf(false) }
    var abonoAmount by remember { mutableFloatStateOf(100f) }
    var abonoStatus by remember { mutableStateOf<String?>(null) } // "success" or "error"

    val clipboardManager = LocalClipboardManager.current

    // Dialog: Abono to savings confirmation
    if (showAbonoDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAbonoDialog = false
                abonoStatus = null
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (abonoStatus != null) {
                            showAbonoDialog = false
                            abonoStatus = null
                        } else {
                            if (localCorrienteBalance >= abonoAmount) {
                                localCorrienteBalance -= abonoAmount
                                localAhorroBalance += abonoAmount
                                abonoStatus = "success"
                            } else {
                                abonoStatus = "error"
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(if (abonoStatus != null) "Cerrar" else "Confirmar Abono", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                if (abonoStatus == null) {
                    TextButton(onClick = { showAbonoDialog = false }) {
                        Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Text(
                    text = if (abonoStatus == "success") "¡Abono Exitoso!" else if (abonoStatus == "error") "Error de Saldo" else "Confirmar Ahorro",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (abonoStatus == "success") {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🌳", fontSize = 48.sp)
                            Text("Has abonado S/ %,.2f a tu meta.".format(abonoAmount), color = VerdeBosqueOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
                            Text("Tu saldo corriente actual se ha actualizado.", color = GrisSage, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    } else if (abonoStatus == "error") {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, null, tint = RojoError, modifier = Modifier.size(48.dp))
                            Text("Saldo insuficiente en tu Cuenta Corriente para completar este abono.", color = RojoError, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
                        }
                    } else {
                        Text("Ingresa el monto a transferir desde tu Cuenta Corriente hacia tu Cuenta de Ahorro.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        Text(
                            text = "S/ %,.0f".format(abonoAmount),
                            color = VerdeBotonForest,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Slider(
                            value = abonoAmount,
                            onValueChange = { abonoAmount = it },
                            valueRange = 10f..1000f,
                            steps = 98,
                            colors = SliderDefaults.colors(
                                thumbColor = VerdeBotonForest,
                                activeTrackColor = VerdeBotonForest,
                                inactiveTrackColor = BordeSuave
                            )
                        )

                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                            Text("S/ 10", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("S/ 1,000", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }

    // Dialog: Copy account number custom toast confirmation
    if (showCopyToast) {
        AlertDialog(
            onDismissRequest = { showCopyToast = false },
            confirmButton = {
                Button(
                    onClick = { showCopyToast = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text("Número Copiado", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            },
            text = {
                Text("El número de cuenta corriente ha sido copiado a tu portapapeles de manera exitosa.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        )
    }

    // --- Main Layout ---
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 112.dp)
    ) {
        // App Bar Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = BlancoPuro,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                    onClick = { navController.popBackStack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Detalle de Cuentas",
                    color = VerdeBosqueOscuro,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Segmented Capsule Tab Selection
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                shape = RoundedCornerShape(50.dp),
                color = BordeSuave,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 0: Corriente
                    Surface(
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = if (selectedTab == 0) BlancoPuro else Color.Transparent,
                        onClick = { selectedTab = 0 }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Cuenta Corriente",
                                color = if (selectedTab == 0) VerdeBosqueOscuro else GrisSage,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Tab 1: Ahorro
                    Surface(
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = if (selectedTab == 1) BlancoPuro else Color.Transparent,
                        onClick = { selectedTab = 1 }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Meta de Ahorros",
                                color = if (selectedTab == 1) VerdeBosqueOscuro else GrisSage,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // TAB 0: CUENTA CORRIENTE DETAILS
        if (selectedTab == 0) {
            when (val state = cuentasState) {
                is DataUiState.Loading -> item { Box(Modifier.fillMaxWidth().padding(44.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = VerdeBotonForest) } }
                is DataUiState.Error -> item { Text(state.mensaje, color = RojoError, modifier = Modifier.padding(16.dp)) }
                is DataUiState.Success -> {
                    val cuentasList = state.data
                    val corriente = cuentasList.firstOrNull { it.tipo == "corriente" }
                    if (corriente == null) {
                        item { Text("No cuentas con cuentas corrientes activas.", color = GrisSage, modifier = Modifier.padding(16.dp)) }
                    } else {
                        // Current checking details card
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                shape = RoundedCornerShape(28.dp),
                                color = BlancoPuro,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        text = "CUENTA CORRIENTE GNB",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = VerdeBotonForest,
                                        letterSpacing = 1.sp
                                    )
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = corriente.numeroCuenta,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GrisSage
                                        )
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(corriente.numeroCuenta))
                                                showCopyToast = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Outlined.ContentCopy, "Copiar número de cuenta", tint = VerdeBotonForest, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(Modifier.height(20.dp))
                                    
                                    Text("Saldo disponible", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "S/ %,.2f".format(localCorrienteBalance),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = VerdeBosqueOscuro,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        // Search box filter
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(52.dp).padding(vertical = 4.dp),
                                shape = RoundedCornerShape(50.dp),
                                color = BlancoPuro,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Search, null, tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = { Text("Filtrar movimientos...", color = GrisSage.copy(0.7f), fontSize = 13.sp) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedTextColor = VerdeBosqueOscuro,
                                            unfocusedTextColor = VerdeBosqueOscuro
                                        ),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    )
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Close, null, tint = GrisSage, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Detailed list header
                        item {
                            Text(
                                "Movimientos de la cuenta",
                                color = VerdeBosqueOscuro,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(start = 6.dp, top = 12.dp, bottom = 4.dp)
                            )
                        }

                        // Dynamic filtered movements render
                        when (val txState = transaccionesState) {
                            is DataUiState.Loading -> item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = VerdeBotonForest) } }
                            is DataUiState.Error -> item { Text(txState.mensaje, color = RojoError, modifier = Modifier.padding(16.dp)) }
                            is DataUiState.Success -> {
                                val allTx = txState.data
                                val filteredTx = allTx.filter {
                                    it.descripcion.contains(searchQuery, ignoreCase = true)
                                }

                                if (filteredTx.isEmpty()) {
                                    item {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            shape = RoundedCornerShape(24.dp),
                                            color = BlancoPuro,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                                        ) {
                                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "No se encontraron movimientos coincidentes",
                                                    color = GrisSage,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    item {
                                        Surface(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            shape = RoundedCornerShape(28.dp),
                                            color = BlancoPuro,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                                        ) {
                                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                                filteredTx.forEachIndexed { index, tx ->
                                                    val esGasto = tx.esDebito()
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
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
                                                                text = tx.descripcion,
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = VerdeBosqueOscuro,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Text(
                                                                text = tx.fecha.take(10),
                                                                fontSize = 12.sp,
                                                                color = GrisSage,
                                                                modifier = Modifier.padding(top = 2.dp),
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                        Text(
                                                            text = "${if (esGasto) "-" else "+"} S/ %,.2f".format(tx.monto),
                                                            color = if (esGasto) RojoError else VerdeBotonForest,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.ExtraBold
                                                        )
                                                    }
                                                    if (index < filteredTx.lastIndex) {
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
                    }
                }
            }
        }

        // TAB 1: SAVINGS GOAL DETAILS
        if (selectedTab == 1) {
            when (val state = ahorroState) {
                is DataUiState.Loading -> item { Box(Modifier.fillMaxWidth().padding(44.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = VerdeBotonForest) } }
                is DataUiState.Error -> item { Text(state.mensaje, color = RojoError, modifier = Modifier.padding(16.dp)) }
                is DataUiState.Success -> {
                    val ahorro = state.data
                    if (ahorro == null) {
                        item { Text("No cuentas con una Meta de Ahorros activa.", color = GrisSage, modifier = Modifier.padding(16.dp)) }
                    } else {
                        // Savings Goal summary card
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                shape = RoundedCornerShape(28.dp),
                                color = BlancoPuro,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                            ) {
                                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "META DE AHORROS GNB",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = VerdeBotonForest,
                                        letterSpacing = 1.sp,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                    
                                    Spacer(Modifier.height(20.dp))
                                    
                                    val progressPercentage = (localAhorroBalance / localAhorroMeta).coerceIn(0.0, 1.0).toFloat()

                                    // Beautiful circle progress target
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(160.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            progress = { progressPercentage },
                                            modifier = Modifier.size(140.dp),
                                            color = VerdeBotonForest,
                                            strokeWidth = 10.dp,
                                            trackColor = BordeSuave
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "%.1f%%".format(progressPercentage * 100),
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = VerdeBosqueOscuro
                                            )
                                            Text(
                                                text = "Completado",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GrisSage,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(24.dp))

                                    // Details list
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                        Text("Saldo ahorrado:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("S/ %,.2f".format(localAhorroBalance), color = VerdeBosqueOscuro, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                        Text("Meta total propuesta:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("S/ %,.2f".format(localAhorroMeta), color = VerdeBosqueOscuro, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                        Text("Tasa de Interés:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("%.2f%% E.A.".format(ahorro.tasaInteres), color = VerdeBotonForest, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                        Text("Fecha de apertura:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(ahorro.fechaApertura.take(10), color = VerdeBosqueOscuro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // CTA Button to simulated savings abono
                        item {
                            Button(
                                onClick = { showAbonoDialog = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 4.dp),
                                shape = RoundedCornerShape(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Savings, null, modifier = Modifier.size(18.dp), tint = Color.White)
                                    Text("Abonar a mi Meta de Ahorros", fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
