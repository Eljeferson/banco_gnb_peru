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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appbanco_s8.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(token: String, navController: NavHostController) {
    // --- Interactive States ---
    var tokenDigitalActive by remember { mutableStateOf(true) }
    var biometricActive by remember { mutableStateOf(false) }
    var hideBalancesActive by remember { mutableStateOf(false) }

    // --- Interactive Dialogs States ---
    var showBiometricDialog by remember { mutableStateOf(false) }
    var biometricSuccess by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLimitsDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // --- Limits Slider state ---
    var dailyLimit by remember { mutableFloatStateOf(5000f) }

    // --- Biometric Simulation Process ---
    LaunchedEffect(showBiometricDialog) {
        if (showBiometricDialog) {
            biometricSuccess = false
            kotlinx.coroutines.delay(2000) // Simulates holding finger on reader
            biometricSuccess = true
            kotlinx.coroutines.delay(800)
            showBiometricDialog = false
            biometricActive = true
        }
    }

    // --- Dialogs Rendering ---
    // Biometric Simulation Dialog
    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = { showBiometricDialog = false },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showBiometricDialog = false }) {
                    Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Autenticación Biométrica GNB",
                    color = VerdeBosqueOscuro,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Coloca tu huella dactilar sobre el sensor del dispositivo.",
                        color = GrisSage,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(if (biometricSuccess) VerdeSage else FondoCrema, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (biometricSuccess) Icons.Default.CheckCircle else Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = if (biometricSuccess) VerdeBotonForest else VerdeBosqueOscuro,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    Text(
                        text = if (biometricSuccess) "¡Huella reconocida con éxito!" else "Escaneando...",
                        color = if (biometricSuccess) VerdeBotonForest else GrisSage,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }

    // Password Changer Dialog
    if (showPasswordDialog) {
        var currentPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var repeatPass by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        var successMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (successMessage.isNotEmpty()) {
                            showPasswordDialog = false
                        } else {
                            if (currentPass.isEmpty() || newPass.isEmpty() || repeatPass.isEmpty()) {
                                errorMessage = "Por favor, completa todos los campos."
                            } else if (newPass.length < 6) {
                                errorMessage = "La contraseña debe tener mínimo 6 caracteres."
                            } else if (newPass != repeatPass) {
                                errorMessage = "Las contraseñas nuevas no coinciden."
                            } else {
                                errorMessage = ""
                                successMessage = "¡Contraseña actualizada con éxito!"
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text(if (successMessage.isNotEmpty()) "Cerrar" else "Actualizar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                if (successMessage.isEmpty()) {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                    }
                }
            },
            title = {
                Text("Cambiar mi Contraseña", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (successMessage.isNotEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = VerdeBotonForest, modifier = Modifier.size(54.dp))
                            Text(successMessage, color = VerdeBosqueOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else {
                        Text("Ingresa tus datos para cambiar tu clave de internet segura.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(
                            value = currentPass,
                            onValueChange = { currentPass = it },
                            label = { Text("Contraseña actual") },
                            visualTransformation = PasswordVisualTransformation(),
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
                            value = newPass,
                            onValueChange = { newPass = it },
                            label = { Text("Nueva contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
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
                            value = repeatPass,
                            onValueChange = { repeatPass = it },
                            label = { Text("Confirmar nueva contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VerdeBotonForest,
                                unfocusedBorderColor = BordeSuave,
                                focusedTextColor = VerdeBosqueOscuro,
                                unfocusedTextColor = VerdeBosqueOscuro
                            )
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(errorMessage, color = RojoError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )
    }

    // Daily Limit Slider Dialog
    if (showLimitsDialog) {
        var tempLimit by remember { mutableFloatStateOf(dailyLimit) }
        AlertDialog(
            onDismissRequest = { showLimitsDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        dailyLimit = tempLimit
                        showLimitsDialog = false
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Guardar Límite", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLimitsDialog = false }) {
                    Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("Límites Diarios de Operación", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Establece el monto máximo de transferencias diarias en tu banca móvil.",
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "S/ %,.0f".format(tempLimit),
                        color = VerdeBotonForest,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Slider(
                        value = tempLimit,
                        onValueChange = { tempLimit = it },
                        valueRange = 500f..15000f,
                        steps = 29,
                        colors = SliderDefaults.colors(
                            thumbColor = VerdeBotonForest,
                            activeTrackColor = VerdeBotonForest,
                            inactiveTrackColor = BordeSuave
                        )
                    )

                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("S/ 500", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("S/ 15,000", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoError)
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancelar", color = GrisSage, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("Cerrar Sesión Segura", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Text("¿Estás seguro de que deseas salir del aplicativo GNB? Deberás ingresar tus credenciales de acceso nuevamente.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        )
    }

    // --- Main Layout ---
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Mi Perfil GNB",
                    color = VerdeBosqueOscuro,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Profile Avatar Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(VerdeSage, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GB",
                            color = VerdeBosqueOscuro,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Giselle N. Benavides",
                        color = VerdeBosqueOscuro,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "giselle.benavides@gnb.com.pe",
                        color = GrisSage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = VerdeSage,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                    ) {
                        Text(
                            text = "GNB PLATINUM VIP MEMBER",
                            color = VerdeBosqueOscuro,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Security Options Panel
        item {
            Text(
                text = "Seguridad y Preferencias",
                color = VerdeBosqueOscuro,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 6.dp, top = 8.dp)
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column {
                    // Token Digital switch row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(Icons.Outlined.Shield, null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Token Digital Activo", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Autorización segura al instante", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Switch(
                            checked = tokenDigitalActive,
                            onCheckedChange = { tokenDigitalActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VerdeBotonForest,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = BordeSuave
                            )
                        )
                    }

                    HorizontalDivider(color = BordeSuave, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 20.dp))

                    // Biometrics switch row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(Icons.Outlined.Fingerprint, null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Inicio Biométrico", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Ingreso con huella dactilar", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Switch(
                            checked = biometricActive,
                            onCheckedChange = {
                                if (it) {
                                    showBiometricDialog = true
                                } else {
                                    biometricActive = false
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VerdeBotonForest,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = BordeSuave
                            )
                        )
                    }

                    HorizontalDivider(color = BordeSuave, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 20.dp))

                    // Hide balance switch row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(Icons.Outlined.VisibilityOff, null, tint = VerdeBotonForest, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Ocultar saldos", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Esconde saldos en pantalla principal", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Switch(
                            checked = hideBalancesActive,
                            onCheckedChange = { hideBalancesActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VerdeBotonForest,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = BordeSuave
                            )
                        )
                    }
                }
            }
        }

        // Account management panel
        item {
            Text(
                text = "Gestión de Accesos",
                color = VerdeBosqueOscuro,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 6.dp, top = 8.dp)
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = BlancoPuro,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Column {
                    // Password Change
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showPasswordDialog = true }.padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(Icons.Outlined.Lock, null, tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                            Text("Cambiar Contraseña", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = GrisSage, modifier = Modifier.size(20.dp))
                    }

                    HorizontalDivider(color = BordeSuave, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 20.dp))

                    // Account Limit settings
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showLimitsDialog = true }.padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(Icons.Outlined.Settings, null, tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Ajustes de Límites Diarios", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Máximo actual: S/ %,.0f".format(dailyLimit), color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = GrisSage, modifier = Modifier.size(20.dp))
                    }

                    HorizontalDivider(color = BordeSuave, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 20.dp))

                    // GNB points tracking row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(Icons.Outlined.Stars, null, tint = VerdeBosqueOscuro, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Mis Puntos GNB", color = VerdeBosqueOscuro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Acumula en tus consumos cotidianos", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        Text("12,450 pts", color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Logout Secure Button
        item {
            Button(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(top = 4.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RojoError.copy(0.1f), contentColor = RojoError),
                border = androidx.compose.foundation.BorderStroke(1.dp, RojoError.copy(0.4f))
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Logout, null, modifier = Modifier.size(18.dp))
                    Text("Cerrar Sesión Segura", fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}
