package com.app.drivertracking.data.models.response.success

data class GetRouteId(
    val bus_route_list: BusRouteList,
    val flag: String
)