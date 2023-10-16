package com.app.drivertracking.data.models.response.success

data class Route(
    val agency_id: Any,
    val bus_id: Int,
    val color: String,
    val created_at: String,
    val description: String,
    val direction_id: Any,
    val estimated_duration: String,
    val id: Int,
    val route_title: String,
    val travel_id: Int,
    val trip_distance: String,
    val type: Any,
    val updated_at: String
)