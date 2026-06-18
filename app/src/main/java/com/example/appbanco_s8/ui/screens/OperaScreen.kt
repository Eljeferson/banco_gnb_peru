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
fun OperaScreen(token: String, navController: NavHostController) {
    // --- Interactive States ---
    var showTransferDialog by remember { mutableStateOf(false) }
    var showServiceDialog by remember { mutableStateOf(false) }
    var showRecargaDialog by remember { mutableStateOf(false) }
    var showPlinDialog by remember { mutableStateOf(false) }
    
    // --- Transfer simulation states ---
    var transferStep by remember { mutableIntStateOf(1) } // 1 = Form, 2 = Receipt
    var txBank by remember { mutableStateOf("GNB") }
    var txAccount by remember { mutableStateOf("") }
    var txName by remember { mutableStateOf("") }
    var txAmount by remember { mutableStateOf("") }
    var txError by remember { mutableStateOf("") }
    var generatedTxId by remember { mutableStateOf("") }

    // --- Service Payment simulation states ---
    var serviceStep by remember { mutableIntStateOf(1) } // 1 = Select, 2 = Code/Pay, 3 = Receipt
    var selectedService by remember { mutableStateOf("") }
    var clientCode by remember { mutableStateOf("") }
    var serviceDebt by remember { mutableDoubleStateOf(0.0) }
    var serviceError by remember { mutableStateOf("") }

    // --- Mobile Recharge simulation states ---
    var rechargeStep by remember { mutableIntStateOf(1) } // 1 = Form, 2 = Receipt
    var selectedCarrier by remember { mutableStateOf("Movistar") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedRechargeAmount by remember { mutableIntStateOf(10) }
    var rechargeError by remember { mutableStateOf("") }

    // --- Plin/QR state ---
    var showPlinQr by remember { mutableStateOf(false) }

    // Reset helper functions
    val resetTransfer = {
        transferStep = 1
        txBank = "GNB"
        txAccount = ""
        txName = ""
        txAmount = ""
        txError = ""
        generatedTxId = ""
    }

    val resetService = {
        serviceStep = 1
        selectedService = ""
        clientCode = ""
        serviceDebt = 0.0
        serviceError = ""
    }

    val resetRecharge = {
        rechargeStep = 1
        selectedCarrier = "Movistar"
        phoneNumber = ""
        selectedRechargeAmount = 10
        rechargeError = ""
    }

    // ── Dialog: TRANSFERENCIAS ──────────────────────────────
    if (showTransferDialog) {
        AlertDialog(
            onDismissRequest = { 
                showTransferDialog = false
                resetTransfer()
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (transferStep == 2) {
                            showTransferDialog = false
                            resetTransfer()
                        } else {
                            if (txAccount.isEmpty() || txName.isEmpty() || txAmount.isEmpty()) {
                                txError = "Completa todos los campos."
                            } else if (txAmount.toDoubleOrNull() == null || txAmount.toDouble() <= 0) {
                                txError = "Monto a transferir no es válido."
                            } else {
                                txError = ""
                                generatedTxId = "GNB-" + (100000 + (Math.random() * 899999).toInt())
                                transferStep = 2
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(if (transferStep == 2) "Terminar" else "Confirmar Transferencia", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                if (transferStep == 1) {
                    TextButton(onClick = { showTransferDialog = false; resetTransfer() }) {
                        Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Text(
                    text = if (transferStep == 2) "¡Transferencia Exitosa!" else "Nueva Transferencia",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (transferStep == 2) {
                        // Transfer Receipt
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text("🌳", fontSize = 48.sp)
                            Text("Constancia de Transferencia", color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            
                            Surface(
                                color = FondoCrema,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Banco Destino:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(txBank, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Destinatario:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(txName, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Cuenta / CCI:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(txAccount, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Código de Operación:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(generatedTxId, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Monto debitado:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("S/ %,.2f".format(txAmount.toDouble()), color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    } else {
                        // Transfer Input Form
                        Text("Ingresa los datos para realizar una transferencia bancaria rápida.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        // Select destination bank
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("GNB", "BCP", "BBVA", "Interbank").forEach { bank ->
                                FilterChip(
                                    selected = txBank == bank,
                                    onClick = { txBank = bank },
                                    label = { Text(bank, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VerdeSage,
                                        selectedLabelColor = VerdeBosqueOscuro,
                                        containerColor = FondoCrema,
                                        labelColor = GrisSage
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = txAccount,
                            onValueChange = { txAccount = it },
                            label = { Text("Número de Cuenta o CCI") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        OutlinedTextField(
                            value = txName,
                            onValueChange = { txName = it },
                            label = { Text("Nombre del Destinatario") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        OutlinedTextField(
                            value = txAmount,
                            onValueChange = { txAmount = it },
                            label = { Text("Monto a transferir (S/)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        if (txError.isNotEmpty()) {
                            Text(txError, color = RojoError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }

    // ── Dialog: PAGAR SERVICIO ──────────────────────────────
    if (showServiceDialog) {
        AlertDialog(
            onDismissRequest = { 
                showServiceDialog = false
                resetService()
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (serviceStep == 3) {
                            showServiceDialog = false
                            resetService()
                        } else if (serviceStep == 1) {
                            if (selectedService.isEmpty()) {
                                serviceError = "Selecciona un servicio."
                            } else {
                                serviceError = ""
                                serviceDebt = 40.0 + (Math.random() * 200.0).toInt()
                                serviceStep = 2
                            }
                        } else {
                            if (clientCode.isEmpty()) {
                                serviceError = "Ingresa tu código de cliente."
                            } else {
                                serviceError = ""
                                serviceStep = 3
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(
                        text = if (serviceStep == 3) "Terminar" else if (serviceStep == 2) "Confirmar Pago (S/ %,.2f)".format(serviceDebt) else "Siguiente",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                if (serviceStep < 3) {
                    TextButton(onClick = { showServiceDialog = false; resetService() }) {
                        Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Text(
                    text = if (serviceStep == 3) "¡Pago Exitoso!" else "Pagar mis Servicios",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (serviceStep == 1) {
                        Text("Selecciona la empresa prestadora de servicios que deseas pagar:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        listOf("Luz del Sur", "Enel", "Sedapal", "Cálidda Gas", "Movistar Hogar", "Claro Fibra").forEach { service ->
                            Surface(
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = if (selectedService == service) VerdeSage else FondoCrema,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedService == service) VerdeBotonForest else BordeSuave),
                                onClick = { selectedService = service }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (selectedService == service) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (selectedService == service) VerdeBotonForest else GrisSage,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(service, color = VerdeBosqueOscuro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (serviceError.isNotEmpty()) {
                            Text(serviceError, color = RojoError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (serviceStep == 2) {
                        Text("Servicio: $selectedService", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        Text("Ingresa tu código de cliente o suministro para verificar tu deuda:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = clientCode,
                            onValueChange = { clientCode = it },
                            label = { Text("Código de Suministro / Cliente") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text("Total Deuda Pendiente:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("S/ %,.2f".format(serviceDebt), color = RojoError, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        if (serviceError.isNotEmpty()) {
                            Text(serviceError, color = RojoError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Payment Success Screen
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text("🎉", fontSize = 48.sp)
                            Text("¡Pago Procesado Exitosamente!", color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            
                            Surface(
                                color = FondoCrema,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Servicio:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(selectedService, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Suministro:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(clientCode, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Monto Cobrado:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("S/ %,.2f".format(serviceDebt), color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Transacción:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("SERV-" + (100000 + (Math.random() * 899999).toInt()), color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // ── Dialog: RECARGAR CELULAR ──────────────────────────────
    if (showRecargaDialog) {
        AlertDialog(
            onDismissRequest = { 
                showRecargaDialog = false
                resetRecharge()
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rechargeStep == 2) {
                            showRecargaDialog = false
                            resetRecharge()
                        } else {
                            if (phoneNumber.length != 9 || phoneNumber.toLongOrNull() == null) {
                                rechargeError = "El número telefónico debe tener exactamente 9 dígitos numéricos."
                            } else {
                                rechargeError = ""
                                rechargeStep = 2
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(if (rechargeStep == 2) "Terminar" else "Confirmar Recarga", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                if (rechargeStep == 1) {
                    TextButton(onClick = { showRecargaDialog = false; resetRecharge() }) {
                        Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Text(
                    text = if (rechargeStep == 2) "¡Recarga Completada!" else "Recargas Virtuales",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (rechargeStep == 2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text("📱", fontSize = 48.sp)
                            Text("Recarga de Saldo Exitosa", color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            
                            Surface(
                                color = FondoCrema,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Operador:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(selectedCarrier, color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Teléfono:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("+51 $phoneNumber", color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Monto Recargado:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("S/ %,.2f".format(selectedRechargeAmount.toDouble()), color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                        Text("Código de pago:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("REC-" + (100000 + (Math.random() * 899999).toInt()), color = VerdeBosqueOscuro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Selecciona tu operador e ingresa el número móvil de 9 dígitos a recargar:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        // Carrier Chips Selection
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("Movistar", "Claro", "Entel", "Bitel").forEach { carrier ->
                                FilterChip(
                                    selected = selectedCarrier == carrier,
                                    onClick = { selectedCarrier = carrier },
                                    label = { Text(carrier, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VerdeSage,
                                        selectedLabelColor = VerdeBosqueOscuro,
                                        containerColor = FondoCrema,
                                        labelColor = GrisSage
                                    )
                                )
                            }
                        }

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Número Telefónico") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        Spacer(Modifier.height(6.dp))
                        Text("Selecciona el monto:", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf(5, 10, 20, 50).forEach { amt ->
                                FilterChip(
                                    selected = selectedRechargeAmount == amt,
                                    onClick = { selectedRechargeAmount = amt },
                                    label = { Text("S/ $amt", fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VerdeSage,
                                        selectedLabelColor = VerdeBosqueOscuro,
                                        containerColor = FondoCrema,
                                        labelColor = GrisSage
                                    )
                                )
                            }
                        }

                        if (rechargeError.isNotEmpty()) {
                            Text(rechargeError, color = RojoError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }

    // ── Dialog: PLIN ──────────────────────────────────────────
    if (showPlinDialog) {
        var plinPhone by remember { mutableStateOf("") }
        var plinAmount by remember { mutableStateOf("") }
        var plinContactName by remember { mutableStateOf("") }
        var plinError by remember { mutableStateOf("") }
        var plinSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { 
                showPlinDialog = false
                plinPhone = ""
                plinAmount = ""
                plinContactName = ""
                plinError = ""
                plinSuccess = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (plinSuccess) {
                            showPlinDialog = false
                            plinPhone = ""
                            plinAmount = ""
                            plinContactName = ""
                            plinError = ""
                            plinSuccess = false
                        } else {
                            if (plinPhone.length != 9 || plinPhone.toLongOrNull() == null) {
                                plinError = "Número telefónico PLIN no es válido (9 dígitos)."
                            } else if (plinAmount.toDoubleOrNull() == null || plinAmount.toDouble() <= 0) {
                                plinError = "El monto ingresado no es válido."
                            } else {
                                plinError = ""
                                plinContactName = "Martín Paredes G."
                                plinSuccess = true
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(if (plinSuccess) "Cerrar" else "Enviar por PLIN", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                if (!plinSuccess) {
                    TextButton(onClick = { showPlinDialog = false }) {
                        Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Text("Transferir por PLIN", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (plinSuccess) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Text("💸", fontSize = 48.sp)
                            Text("¡Envío PLIN Completado!", color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text("Has enviado S/ %,.2f a $plinContactName (+51 $plinPhone) al instante.".format(plinAmount.toDouble()), color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                    } else {
                        Text("Envía dinero instantáneamente a cualquier celular afiliado a PLIN.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = plinPhone,
                            onValueChange = { plinPhone = it },
                            label = { Text("Número Celular Afiliado (PLIN)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        OutlinedTextField(
                            value = plinAmount,
                            onValueChange = { plinAmount = it },
                            label = { Text("Monto a enviar (S/)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        if (plinError.isNotEmpty()) {
                            Text(plinError, color = RojoError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }

    // --- Main UI Grid ---
    data class OperaItem(val icon: ImageVector, val label: String)

    val operacionesCuenta = listOf(
        OperaItem(Icons.Default.SwapHoriz,        "Transferir"),
        OperaItem(Icons.Default.PhoneAndroid,      "Recargar"),
        OperaItem(Icons.Default.Receipt,           "Pagar servicio"),
        OperaItem(Icons.Default.PhoneIphone,       "PLIN"),
        OperaItem(Icons.Default.MoneyOff,          "Retiro sin tarjeta"),
        OperaItem(Icons.Default.Description,       "Ver estado"),
        OperaItem(Icons.Default.CreditCard,        "Pagar tarjeta"),
        OperaItem(Icons.Default.CardGiftcard,      "Tarjeta regalo"),
        OperaItem(Icons.Default.CurrencyExchange,  "T-Cambio"),
        OperaItem(Icons.Default.Link,              "Vincular"),
        OperaItem(Icons.Default.FlightTakeoff,     "Al exterior")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 112.dp)
    ) {
        item {
            Text(
                text       = "Operaciones GNB",
                color      = VerdeBosqueOscuro,
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                modifier   = Modifier.padding(bottom = 16.dp)
            )
        }

        // Section Title
        item {
            Row(modifier = Modifier.padding(bottom = 12.dp)) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = VerdeSage,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                ) {
                    Text(
                        text     = "ACCIONES RÁPIDAS Y PAGOS",
                        color    = VerdeBosqueOscuro,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Premium Grid layout
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
                    val rows = operacionesCuenta.chunked(3)
                    rows.forEachIndexed { rowIndex, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { item ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { 
                                            when (item.label) {
                                                "Transferir" -> showTransferDialog = true
                                                "Pagar servicio" -> showServiceDialog = true
                                                "Recargar" -> showRecargaDialog = true
                                                "PLIN" -> showPlinDialog = true
                                                else -> {
                                                    // Trigger simulated generic dialog
                                                    showTransferDialog = true
                                                    txName = "Operativa GNB Simulación"
                                                    txAccount = "191-482910-3-12"
                                                    txAmount = "150.00"
                                                }
                                            }
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        modifier = Modifier.size(54.dp),
                                        shape = CircleShape,
                                        color = VerdeSage,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector        = item.icon,
                                                contentDescription = item.label,
                                                tint               = VerdeBotonForest,
                                                modifier           = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text      = item.label,
                                        color     = VerdeBosqueOscuro,
                                        fontSize  = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines  = 2,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // Rellenar si la fila no tiene 3 elementos
                            repeat(3 - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
