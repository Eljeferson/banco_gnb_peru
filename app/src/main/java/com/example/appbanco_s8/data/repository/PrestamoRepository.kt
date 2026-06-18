
package com.example.appbanco_s8.data.repository

import com.example.appbanco_s8.data.model.Prestamo
import com.example.appbanco_s8.data.remote.RetrofitClient

class PrestamoRepository {
    private val api = RetrofitClient.api

    suspend fun getPrestamos(token: String): Result<List<Prestamo>> = try {
        val r = api.getPrestamos("Bearer $token")
        if (r.isSuccessful) Result.success(r.body() ?: emptyList())
        else Result.failure(Exception("Error ${r.code()}"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun createSolicitud(token: String, request: com.example.appbanco_s8.data.model.SolicitudCreditoRequest): Result<Boolean> = try {
        val r = api.createSolicitudCredito("Bearer $token", request = request)
        if (r.isSuccessful) Result.success(true)
        else Result.failure(Exception("Error ${r.code()}: ${r.errorBody()?.string()}"))
    } catch (e: Exception) { Result.failure(e) }
}