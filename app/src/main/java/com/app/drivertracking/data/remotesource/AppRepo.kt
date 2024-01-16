package com.app.drivertracking.data.remotesource

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

interface AppRepo {

    suspend fun driverAuth(driverAuthRequest: DriverAuthRequest):Response<GetDriverLogin>

    suspend fun driverProfile(profileRequest: ProfileRequest):Response<GetDriverProfileX>

    suspend fun busRouteById(routeRequest: RouteRequest):Response<GetTravel>

    suspend fun routeStopById(stopRequest: StopRequest):Response<GetRouteStopList>

    suspend fun addLocation(location: GetAddLocationResponse): Response<DataLocation>

}