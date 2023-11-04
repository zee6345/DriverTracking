package com.app.drivertracking.data.models.response.success

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


 class DataLocation {
    @SerializedName("lat")
    @Expose
    var lat: String? = null

    @SerializedName("long")
    @Expose
    var long: String? = null

    @SerializedName("bus_id")
    @Expose
    var busId: String? = null

    @SerializedName("route_id")
    @Expose
    var routeId: String? = null

    @SerializedName("bus_number_plate")
    @Expose
    var busNumberPlate: String? = null

    @SerializedName("travel_id")
    @Expose
    var travelId: String? = null

    @SerializedName("bus_status")
    @Expose
    var busStatus: String? = null
}

class Status {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("data")
    @Expose
    var data: DataLocation? = null
}