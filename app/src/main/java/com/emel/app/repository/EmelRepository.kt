package com.emel.app.repository

import androidx.lifecycle.LiveData
import com.emel.app.network.api.ApiService
import com.emel.app.network.api.adapter.AppExecutors
import com.emel.app.network.api.adapter.NetworkBoundResource
import com.emel.app.network.api.adapter.Resource
import com.emel.app.network.api.requests.LoginRequest
import com.emel.app.network.api.requests.TokenRequest
import com.emel.app.network.api.responses.LoginResponse
import com.emel.app.network.api.responses.TokenResponse
import com.emel.app.network.model.Malfunction
import com.emel.app.network.model.ParkingMeter
import okhttp3.Credentials
import javax.inject.Inject

class EmelRepository @Inject constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors
) {

    fun login(loginRequest: LoginRequest): LiveData<Resource<LoginResponse>> {
        return object : NetworkBoundResource<LoginResponse>(appExecutors) {
            override fun createCall() = apiService.login(loginRequest)
        }.asLiveData()
    }

    fun getAllParkingMeters(token: String): LiveData<Resource<List<ParkingMeter>>> {
        return object : NetworkBoundResource<List<ParkingMeter>>(appExecutors) {
            override fun createCall() = apiService.getAllParkingMeters(token)
        }.asLiveData()
    }

    fun refreshToken(token: String, tokenRequest: TokenRequest): LiveData<Resource<TokenResponse>> {
        return object : NetworkBoundResource<TokenResponse>(appExecutors) {
            override fun createCall() = apiService.refreshToken(token, tokenRequest)
        }.asLiveData()
    }

    fun getMalFunctionsByUser(token: String): LiveData<Resource<List<Malfunction>>> {
        return object : NetworkBoundResource<List<Malfunction>>(appExecutors) {
            override fun createCall() = apiService.getAllAssignedMalfunctions(token)
        }.asLiveData()
    }

    fun updateMalfunctionInParkingMeter(
        token: String,
        id: Int,
        parkingMeter: ParkingMeter
    ): LiveData<Resource<Malfunction>> {
        return object : NetworkBoundResource<Malfunction>(appExecutors) {
            override fun createCall() =
                apiService.updateMalfunctionInParkingMeter(token, id.toString(), parkingMeter)
        }.asLiveData()
    }

    fun updateMalfunction(
        token: String,
        id: Int,
        malfunction: Malfunction
    ): LiveData<Resource<Malfunction>> {
        return object : NetworkBoundResource<Malfunction>(appExecutors) {
            override fun createCall() =
                apiService.updateMalfunction(token, id.toString(), malfunction)
        }.asLiveData()
    }

    fun addMalfunctionToParkingMeter(
        token: String,
        malfunction: Malfunction
    ): LiveData<Resource<Malfunction>> {
        return object : NetworkBoundResource<Malfunction>(appExecutors) {
            override fun createCall() =
                apiService.addMalfunctionToParkingMeter(token, malfunction)
        }.asLiveData()
    }
}