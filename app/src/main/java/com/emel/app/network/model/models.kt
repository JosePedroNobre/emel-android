package com.emel.app.network.model

data class ParkingMeter(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    var status: Int,
    val malfunctions: List<Malfunction>?
)

data class Malfunction(
    val id: Int?,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val creationDate: String?,
    val resolvedDate: String?,
    var status: Int?,
    val parkingMeterId: Int
)