package com.emel.app.ui.flows.main

import androidx.lifecycle.ViewModel
import com.emel.app.network.api.requests.LoginRequest
import com.emel.app.network.model.Malfunction
import com.emel.app.network.model.ParkingMeter
import com.emel.app.repository.EmelRepository
import javax.inject.Inject

class MapFragmentVM @Inject constructor(private val emelRepository: EmelRepository) :
    ViewModel() {

    fun addMaintenanceInParkingMeter(token: String, malfunction: Malfunction) =
        emelRepository.addMalfunctionToParkingMeter(token, malfunction)

    fun updateMalfunctionInParkingMeter(token: String, id: Int, parkingMeter: ParkingMeter) =
        emelRepository.updateMalfunctionInParkingMeter(token, id, parkingMeter)

    fun getParkingMeters(token: String) =
        emelRepository.getAllParkingMeters(token)
}