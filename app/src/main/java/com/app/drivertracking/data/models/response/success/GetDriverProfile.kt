package com.app.drivertracking.data.models.response.success

data class GetDriverProfile(
    val driver_profile: List<DriverProfile>,
    val flag: String
)