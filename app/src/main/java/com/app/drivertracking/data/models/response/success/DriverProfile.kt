package com.app.drivertracking.data.models.response.success

data class DriverProfile(
    val agency_id: Int,
    val bus_id: Int,
    val created_at: String,
    val email: String,
    val id: Int,
    val matricule: String,
    val name: String,
    val phone: String,
    val pin: String,
    val sex: String,
    val updated_at: String
)