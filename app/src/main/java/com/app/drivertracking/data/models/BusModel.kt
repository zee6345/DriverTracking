package com.app.drivertracking.data.models

data class BusModel (
    val bus_id:Int,
    val route_id:Int ?= null,
    val bus_number_plate:Int ?= null,
    val bus_status:Int ?= null
)