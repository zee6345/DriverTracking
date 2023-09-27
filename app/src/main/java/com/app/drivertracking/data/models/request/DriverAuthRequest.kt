package com.app.drivertracking.data.models.request

data class DriverAuthRequest(
    val bus:String,
    val number_plate:String,
    val bus_pin:String
)
