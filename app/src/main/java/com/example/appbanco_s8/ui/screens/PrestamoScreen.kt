package com.example.appbanco_s8.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appbanco_s8.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appbanco_s8.ui.viewmodel.PrestamoViewModel
import com.example.appbanco_s8.ui.viewmodel.DataUiState
import com.example.appbanco_s8.data.model.SolicitudCreditoRequest
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoScreen(token: String, email: String = "", navController: NavHostController, viewModel: PrestamoViewModel = viewModel()) {
    var loanAmount by remember { mutableFloatStateOf(10000f) }
    var selectedMonths by remember { mutableIntStateOf(12) }
    var destino by remember { mutableStateOf("") }
    var tieneGarantia by remember { mutableStateOf(false) }
    var showApprovalDialog by remember { mutableStateOf(false) }

    val solicitudStatus by viewModel.solicitudStatus.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(solicitudStatus) {
        if (solicitudStatus is DataUiState.Success) {
            showApprovalDialog = true
        } else if (solicitudStatus is DataUiState.Error) {
            val errorMsg = (solicitudStatus as DataUiState.Error).mensaje
            android.widget.Toast.makeText(context, "Error: $errorMsg", android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearSolicitudStatus()
        }
    }

    // Simulated monthly payment calculation (15% annual interest rate roughly)
    val monthlyRate = 0.15 / 12
    val monthlyPayment = (loanAmount * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -selectedMonths.toDouble()))

    if (showApprovalDialog) {
        AlertDialog(
            onDismissRequest = { showApprovalDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showApprovalDialog = false
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Volver al Inicio", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("¡Solicitud Enviada!", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🚀", fontSize = 48.sp)
                    Text(
                        text = "Tu solicitud de préstamo por S/ %,.2f ha sido enviada con éxito. Será evaluada pronto.".format(loanAmount),
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Simulador de Préstamos",
                color = VerdeBosqueOscuro,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }

        // Calculator Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "¿Cuánto dinero necesitas?",
                        color = VerdeBosqueOscuro,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "S/ %,.0f".format(loanAmount),
                        color = VerdeBotonForest,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Slider(
                        value = loanAmount,
                        onValueChange = { loanAmount = it },
                        valueRange = 1000f..50000f,
                        steps = 49,
                        colors = SliderDefaults.colors(
                            thumbColor = VerdeBotonForest,
                            activeTrackColor = VerdeBotonForest,
                            inactiveTrackColor = BordeSuave
                        )
                    )

                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("S/ 1,000", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("S/ 50,000", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Term selection Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "¿En cuántos meses deseas pagar?",
                        color = VerdeBosqueOscuro,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(6, 12, 24, 36).forEach { months ->
                            FilterChip(
                                selected = selectedMonths == months,
                                onClick = { selectedMonths = months },
                                label = { Text("$months meses", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VerdeSage,
                                    selectedLabelColor = VerdeBosqueOscuro,
                                    containerColor = FondoCrema,
                                    labelColor = GrisSage
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selectedMonths == months,
                                    borderColor = BordeSuave,
                                    selectedBorderColor = VerdeBotonForest,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.dp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Destino y Garantía
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = destino,
                        onValueChange = { destino = it },
                        label = { Text("Destino del Préstamo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = tieneGarantia,
                            onCheckedChange = { tieneGarantia = it },
                            colors = CheckboxDefaults.colors(checkedColor = VerdeBotonForest)
                        )
                        Text(
                            text = "¿Cuenta con alguna garantía?",
                            color = VerdeBosqueOscuro,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Summary Payment Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = VerdeSage,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Tu cuota mensual aproximada sería:",
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ %,.2f".format(monthlyPayment),
                        color = VerdeBosqueOscuro,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "*TCEA referencial de 15.00%. Sujeto a evaluación de crédito.",
                        color = GrisSage,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Loan CTA
        item {
            Button(
                onClick = {
                    val code = "SOL-" + UUID.randomUUID().toString().take(6).uppercase()
                    val req = SolicitudCreditoRequest(
                        solicitudCodigo = code,
                        asesorId = null,
                        clientDni = email.takeWhile { it != '@' }.takeIf { it.isNotBlank() } ?: "12345678", // Mock DNI from email
                        clientNombre = email.substringBefore("@"),
                        productoTipo = "Crédito Empresarial - Microempresa",
                        montoSolicitado = loanAmount.toDouble(),
                        plazoMeses = selectedMonths,
                        estado = "enviado"
                    )
                    viewModel.enviarSolicitud(token, req)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = solicitudStatus !is DataUiState.Loading,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
            ) {
                if (solicitudStatus is DataUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar Solicitud", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
