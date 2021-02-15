package com.emel.app.network.api

import androidx.lifecycle.LiveData
import com.emel.app.network.api.requests.LoginRequest
import com.emel.app.network.api.requests.TokenRequest
import com.emel.app.network.api.responses.LoginResponse
import com.emel.app.network.api.responses.TokenResponse
import com.emel.app.network.model.ApiResponse
import com.emel.app.network.model.Malfunction
import com.emel.app.network.model.ParkingMeter
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/identity/login")
    fun login(@Body loginRequest: LoginRequest): LiveData<ApiResponse<LoginResponse>>

    @POST("/api/identity/token/refresh")
    fun refreshToken(
        @Header("Authorization") token: String,
        @Body refreshToken: TokenRequest
    ):   LiveData<ApiResponse<TokenResponse>>

    @GET("/api/parking-meter/all")
    fun getAllParkingMeters(
        @Header("Authorization") token: String
    ): LiveData<ApiResponse<List<ParkingMeter>>>

    @GET("/api/malfunction/all")
    fun getAllAssignedMalfunctions(
        @Header("Authorization") token: String
    ): LiveData<ApiResponse<List<Malfunction>>>


    @POST("/api/malfunction")
    fun addMalfunctionToParkingMeter(
        @Header("Authorization") token: String,
        @Body malfunction: Malfunction
    ): LiveData<ApiResponse<Malfunction>>


    @PUT("/api/parking-meter/{id}")
    fun updateMalfunctionInParkingMeter(
        @Header("Authorization") token: String,
        @Path("id", encoded = true) id: String,
        @Body parkingMeter: ParkingMeter
    ): LiveData<ApiResponse<Malfunction>>

    @PUT("/api/malfunction/{id}")
    fun updateMalfunction(
        @Header("Authorization") token: String,
        @Path("id", encoded = true) id: String,
        @Body parkingMeter: Malfunction
    ): LiveData<ApiResponse<Malfunction>>


    @GET("GetListaCartoes?TipoLista=ATIVOS")
    fun getParkingMeters(@Header("Authorization") h1: String): LiveData<ApiResponse<List<ParkingMeter>>>
}