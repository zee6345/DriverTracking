package com.app.drivertracking.data.models.request

data class GetAddLocationResponse(
    val lat: Double,
    val long: Double,
    val bus_id: Int,
    val route_id: Int? = 0,
    val bus_number_plate: Int? = 0,
    val bus_status: Int? = 0,
    val travel_id: Int? = 0
)
