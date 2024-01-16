package com.app.drivertracking.data.models.response.success

data class GetStopsList(
    val flag: String,
    val stop_list: List<Stop>
)