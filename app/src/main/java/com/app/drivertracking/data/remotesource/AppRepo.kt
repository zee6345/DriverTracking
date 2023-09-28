package com.app.drivertracking.data.remotesource

import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import com.app.drivertracking.data.models.response.success.GetRouteId
import com.app.drivertracking.data.models.response.success.GetStopsList
import retrofit2.Response

interface AppRepo {

    suspend fun driverAuth(driverAuthRequest: DriverAuthRequest):Response<GetDriverAuth>

    suspend fun driverProfile(profileRequest: ProfileRequest):Response<GetDriverProfile>

    suspend fun busRouteById(routeRequest: RouteRequest):Response<GetRouteId>

    suspend fun routeStopById(stopRequest: StopRequest):Response<GetStopsList>



}