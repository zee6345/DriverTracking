package com.app.drivertracking.data.api

import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import com.app.drivertracking.data.models.response.success.GetRouteId
import com.app.drivertracking.data.models.response.success.GetStopsList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login_driver")
    suspend fun driverLogin(
        @Body driverAuthRequest: DriverAuthRequest
    ): Response<GetDriverAuth>

    @POST("get_driver_profile")
    suspend fun driverProfile(
        @Body profileRequest: ProfileRequest
    ): Response<GetDriverProfile>

    @POST("get_bus_route")
    suspend fun busRouteById(
        @Body routeRequest: RouteRequest
    ): Response<GetRouteId>

    @POST("get_route_stop_list")
    suspend fun routeStopById(
        @Body stopRequest: StopRequest
    ): Response<GetStopsList>


}