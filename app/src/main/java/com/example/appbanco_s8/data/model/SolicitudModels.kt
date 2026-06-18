package com.example.appbanco_s8.data.model

import com.google.gson.annotations.SerializedName

data class SolicitudCreditoRequest(
    @SerializedName("solicitud_codigo")
    val solicitudCodigo: String,
    @SerializedName("asesor_id")
    val asesorId: String? = null,
    @SerializedName("client_dni")
    val clientDni: String,
    @SerializedName("client_nombre")
    val clientNombre: String,
    @SerializedName("producto_tipo")
    val productoTipo: String,
    @SerializedName("monto_solicitado")
    val montoSolicitado: Double,
    @SerializedName("plazo_meses")
    val plazoMeses: Int,
    @SerializedName("estado")
    val estado: String
)
