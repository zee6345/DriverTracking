package com.app.drivertracking.data.api

import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.GetAddLocationResponse
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.success.DataLocation
import com.app.drivertracking.data.models.response.success.GetDriverLogin
import com.app.drivertracking.data.models.response.success.GetDriverProfileX
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.data.models.response.success.GetTravel
import com.app.drivertracking.data.models.response.success.LocationUpdate
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("login_driver")
    suspend fun driverLogin(
        @Body driverAuthRequest: DriverAuthRequest
    ): Response<GetDriverLogin>

    @POST("get_bus")
    suspend fun driverProfile(
        @Body profileRequest: ProfileRequest
    ): Response<GetDriverProfileX>

    @POST("get_travel")
    suspend fun busRouteById(
        @Body routeRequest: RouteRequest
    ): Response<GetTravel>

    @POST("get_route_stop_list")
    suspend fun routeStopById(
        @Body stopRequest: StopRequest
    ): Response<GetRouteStopList>


    @POST("addlocation")
    suspend fun addlocation(
        @Body location: GetAddLocationResponse
    ):Response<DataLocation>

}