package com.example.appbanco_s8.data.remote

import com.example.appbanco_s8.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SupabaseApi {

    @GET("rest/v1/cuentas")
    suspend fun getCuentas(
        @Header("Authorization") token: String,
        @Query("select") select: String = "*",
        @Query("order")  order:  String = "tipo.asc"
    ): Response<List<Cuenta>>

    @GET("rest/v1/transacciones")
    suspend fun getTransacciones(
        @Header("Authorization") token:    String,
        @Query("cuenta_id")      cuentaId: String,
        @Query("select")         select:   String = "*",
        @Query("order")          order:    String = "fecha.desc",
        @Query("limit")          limit:    Int    = 10
    ): Response<List<Transaccion>>

    @GET("rest/v1/cuentas_ahorro")
    suspend fun getCuentaAhorro(
        @Header("Authorization") token:  String,
        @Query("select")         select: String = "*",
        @Query("limit")          limit:  Int    = 1
    ): Response<List<CuentaAhorro>>

    @GET("rest/v1/tarjetas")
    suspend fun getTarjetas(
        @Header("Authorization") token:  String,
        @Query("select")         select: String = "*"
    ): Response<List<Tarjeta>>

    @GET("rest/v1/prestamos")
    suspend fun getPrestamos(
        @Header("Authorization") token:  String,
        @Query("select")         select: String = "*"
    ): Response<List<Prestamo>>

    @GET("rest/v1/pagos")
    suspend fun getPagos(
        @Header("Authorization") token:  String,
        @Query("select")         select: String = "*",
        @Query("order")          order:  String = "fecha.desc",
        @Query("limit")          limit:  Int    = 20
    ): Response<List<Pago>>
}