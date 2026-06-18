package com.example.appbanco_s8.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appbanco_s8.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ContactoScreen(navController: NavHostController) {
    // --- Interactive States ---
    var showChatbotDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showCobrosDialog by remember { mutableStateOf(false) }

    // --- Chatbot Simulation states ---
    val chatbotMessages = remember { mutableStateListOf<Pair<String, Boolean>>() } // Message, isUser
    var isTyping by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Initialize chatbot welcome
    LaunchedEffect(showChatbotDialog) {
        if (showChatbotDialog && chatbotMessages.isEmpty()) {
            chatbotMessages.add(Pair("¡Hola! Soy BancaSimple, tu asistente virtual de GNB. 🌳 ¿En qué puedo ayudarte hoy?", false))
        }
    }

    // Helper to send chatbot message and simulate automated reply
    val sendChatbotReply: (String, String) -> Unit = { userMsg, botMsg ->
        chatbotMessages.add(Pair(userMsg, true))
        isTyping = true
        // Simulate thinking delay
        scope.launch {
            kotlinx.coroutines.delay(1200)
            isTyping = false
            chatbotMessages.add(Pair(botMsg, false))
        }
    }

    // ── Dialog: CHATBOT ASISTENTE VIRTUAL ──────────────────────────
    if (showChatbotDialog) {
        AlertDialog(
            onDismissRequest = { showChatbotDialog = false },
            confirmButton = {
                Button(
                    onClick = { showChatbotDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Cerrar Chat", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = VerdeSage) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, null, tint = VerdeBotonForest, modifier = Modifier.size(18.dp))
                        }
                    }
                    Column {
                        Text("Asistente BancaSimple", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(if (isTyping) "Escribiendo..." else "En línea", color = if (isTyping) VerdeBotonForest else GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    // Chat Messages Container
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = FondoCrema,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(count = chatbotMessages.size) { index ->
                                val msg = chatbotMessages[index]
                                val text = msg.first
                                val isUser = msg.second
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    Surface(
                                        color = if (isUser) VerdeSage else BlancoPuro,
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 0.dp,
                                            bottomEnd = if (isUser) 0.dp else 16.dp
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                        modifier = Modifier.widthIn(max = 200.dp)
                                    ) {
                                        Text(
                                            text = text,
                                            color = VerdeBosqueOscuro,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(10.dp),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                            if (isTyping) {
                                item {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                        Surface(
                                            color = BlancoPuro,
                                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Text("...", color = GrisSage, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Predefined Quick Replies
                    if (!isTyping) {
                        Text("Sugerencias de consultas:", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50.dp),
                                    color = BlancoPuro,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                    onClick = {
                                        sendChatbotReply(
                                            "Saber mi saldo",
                                            "Tus cuentas tienen saldos completamente seguros. Puedes consultarlos en tiempo real en la pantalla de 'Detalle de Cuentas' desde el Inicio."
                                        )
                                    }
                                ) {
                                    Text("💰 Consultar saldo", color = VerdeBosqueOscuro, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp), textAlign = TextAlign.Center)
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50.dp),
                                    color = BlancoPuro,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                    onClick = {
                                        sendChatbotReply(
                                            "Activar Token Digital",
                                            "Tu Token Digital se encuentra activo. Sirve para autorizar todas tus transferencias y pagos de forma automática y 100% blindada."
                                        )
                                    }
                                ) {
                                    Text("🔑 Token Digital", color = VerdeBosqueOscuro, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp), textAlign = TextAlign.Center)
                                }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50.dp),
                                    color = BlancoPuro,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                    onClick = {
                                        sendChatbotReply(
                                            "Reportar tarjeta",
                                            "Para bloquear o reportar tu tarjeta GNB de forma inmediata por pérdida o robo, llámanos al instante a nuestra Central de Atención al (01) 616-4722."
                                        )
                                    }
                                ) {
                                    Text("🚨 Bloquear Tarjeta", color = VerdeBosqueOscuro, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp), textAlign = TextAlign.Center)
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(50.dp),
                                    color = BlancoPuro,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                                    onClick = {
                                        sendChatbotReply(
                                            "Ver mis puntos GNB",
                                            "Has acumulado un saldo activo de 12,450 puntos GNB. Puedes canjearlos por grandes beneficios desde tu perfil de usuario."
                                        )
                                    }
                                ) {
                                    Text("⭐ Mis Puntos GNB", color = VerdeBosqueOscuro, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp), textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // ── Dialog: PUNTOS DE ATENCIÓN DIRECTORY ──────────────────────────
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            confirmButton = {
                Button(
                    onClick = { showLocationDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text("Puntos de Atención GNB", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Encuentra nuestras sucursales y redes de cajeros automáticos KASNET más cercanas en Lima:", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = FondoCrema,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
                    ) {
                        LazyColumn(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            item {
                                Column {
                                    Text("🏢 Agencia Principal San Isidro", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Text("Av. Rivera Navarrete 475, San Isidro, Lima", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            item { HorizontalDivider(color = BordeSuave) }
                            item {
                                Column {
                                    Text("🏢 Agencia Larco Miraflores", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Text("Av. José Larco 1205, Miraflores, Lima", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            item { HorizontalDivider(color = BordeSuave) }
                            item {
                                Column {
                                    Text("🏢 Agencia Chacarilla Surco", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Text("Av. Primavera 1024, Chacarilla, Santiago de Surco", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            item { HorizontalDivider(color = BordeSuave) }
                            item {
                                Column {
                                    Text("🏪 Cajero Red KASNET - Jockey Plaza", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Text("C.C. Jockey Plaza, Primer Nivel, Surco", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            item { HorizontalDivider(color = BordeSuave) }
                            item {
                                Column {
                                    Text("🏪 Cajero Red KASNET - Plaza Norte", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    Text("C.C. Plaza Norte, Independencia, Lima", color = GrisSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    // ── Dialog: TELEPHONE DIALER CONFIRMATION ──────────────────────────
    if (showPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPhoneDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Llamar Ahora", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhoneDialog = false }) {
                    Text("Cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("Llamada Telefónica GNB", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Text("¿Deseas iniciar una llamada telefónica directa al canal de atención al cliente de Banco GNB Perú al (01) 616-4722?", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        )
    }

    // ── Dialog: ZONA DE COBRO BILLS CHECK ──────────────────────────
    if (showCobrosDialog) {
        AlertDialog(
            onDismissRequest = { showCobrosDialog = false },
            confirmButton = {
                Button(
                    onClick = { showCobrosDialog = false },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBotonForest)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            title = {
                Text("Zona de Cobros Pendientes", color = VerdeBosqueOscuro, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text("🙌", fontSize = 48.sp)
                    Text("¡Todo al día!", color = VerdeBotonForest, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text("Actualmente no tienes recibos, facturas o cobros pendientes en tu Zona de Cobro de Banco GNB.", color = GrisSage, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        )
    }

    // --- Screen Body ---
    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding      = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 112.dp)
    ) {
        item {
            Text(
                text = "Contacto",
                color = VerdeBosqueOscuro,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Card Asistente Virtual
        item {
            ContactoCard(
                icon       = Icons.Default.SmartToy,
                titulo     = "Asistente Virtual",
                descripcion = "BancaSimple está disponible las 24 horas del día para ayudarte a resolver tus consultas de forma inmediata.",
                botonLabel = "Comenzar",
                onClick    = { showChatbotDialog = true },
                botonColor = VerdeBotonForest
            )
        }

        // Card Puntos de atención
        item {
            ContactoCard(
                icon        = Icons.Default.LocationOn,
                titulo      = "Puntos de atención",
                descripcion = "Consulta nuestras Agencias, Cajeros Automáticos y nuestra amplia Red de Agentes KASNET.",
                botonLabel  = "Consultar",
                onClick     = { showLocationDialog = true },
                botonColor  = VerdeBosqueOscuro
            )
        }

        // Card Banca por Teléfono
        item {
            ContactoCard(
                icon        = Icons.Default.Phone,
                titulo      = "Banca por Teléfono",
                descripcion = "Lima: (01) 616-4722 | Provincias: 0801-00088. Horario: Lun a Vie 9:00 a.m. a 6:30 p.m., Sáb 9:00 a.m. a 1:00 p.m.",
                botonLabel  = "Llamar",
                onClick     = { showPhoneDialog = true },
                botonColor  = VerdeBosqueOscuro
            )
        }

        // Card Zona de cobro
        item {
            ContactoCard(
                icon        = Icons.Default.Calculate,
                titulo      = "Zona de cobro",
                descripcion = "Consulta y gestiona tus cobros pendientes de forma segura.",
                botonLabel  = "Ver cobros",
                onClick     = { showCobrosDialog = true },
                botonColor  = VerdeBotonForest
            )
        }
    }
}

@Composable
private fun ContactoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String,
    botonLabel: String,
    onClick: () -> Unit,
    botonColor: Color
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = BlancoPuro,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = VerdeSage,
                border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = titulo,
                        tint               = VerdeBotonForest,
                        modifier           = Modifier.size(26.dp)
                    )
                }
            }
            Text(titulo, color = VerdeBosqueOscuro, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                text = descripcion, 
                color = GrisSage, 
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Button(
                onClick  = onClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(50.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = botonColor)
            ) {
                Text(botonLabel, color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }
    }
}
