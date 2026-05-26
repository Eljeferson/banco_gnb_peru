package com.example.appbanco_s8.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.appbanco_s8.ui.theme.*

@Composable
fun ContactoScreen(navController: NavHostController) {
    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding      = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Contacto",
                color = AzulGNB,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
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
                onClick    = {}
            )
        }

        // Card Puntos de atención
        item {
            ContactoCard(
                icon        = Icons.Default.LocationOn,
                titulo      = "Puntos de atención",
                descripcion = "Consulta nuestras Agencias, Cajeros Automáticos y nuestra amplia Red de Agentes KASNET.",
                botonLabel  = "Consultar",
                onClick     = {}
            )
        }

        // Card Banca por Teléfono
        item {
            ContactoCard(
                icon        = Icons.Default.Phone,
                titulo      = "Banca por Teléfono",
                descripcion = "Lima: (01) 616-4722 | Provincias: 0801-00088. Horario: Lun a Vie 9:00 a.m. a 6:30 p.m., Sáb 9:00 a.m. a 1:00 p.m.",
                botonLabel  = "Llamar",
                onClick     = {},
                botonColor  = AzulGNB
            )
        }

        // Card Zona de cobro
        item {
            ContactoCard(
                icon        = Icons.Default.Calculate,
                titulo      = "Zona de cobro",
                descripcion = "Consulta y gestiona tus cobros pendientes de forma segura.",
                botonLabel  = "Ver cobros",
                onClick     = {}
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun ContactoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    descripcion: String,
    botonLabel: String,
    onClick: () -> Unit,
    botonColor: Color = VerdeGNB
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = BlancoPuro,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier            = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = titulo,
                tint               = VerdeGNB,
                modifier           = Modifier.size(48.dp)
            )
            Text(titulo,      color = AzulGNB, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(descripcion, color = GrisTexto,   fontSize = 13.sp,
                textAlign = TextAlign.Center)
            Button(
                onClick  = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = botonColor)
            ) {
                Text(botonLabel, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
