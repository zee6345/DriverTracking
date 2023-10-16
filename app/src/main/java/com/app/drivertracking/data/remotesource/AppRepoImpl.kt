package com.app.drivertracking.data.remotesource


import com.app.drivertracking.data.api.ApiService
import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverLogin
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import com.app.drivertracking.data.models.response.success.GetDriverProfileX
import com.app.drivertracking.data.models.response.success.GetRouteId
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.data.models.response.success.GetStopsList
import com.app.drivertracking.data.models.response.success.GetTravel
import retrofit2.Response

import javax.inject.Inject

class AppRepoImpl @Inject constructor(private val apiService: ApiService) : AppRepo {

    override suspend fun driverAuth(driverAuthRequest: DriverAuthRequest): Response<GetDriverLogin> {
        return apiService.driverLogin(driverAuthRequest)
    }

    override suspend fun driverProfile(profileRequest: ProfileRequest): Response<GetDriverProfileX> {
        return apiService.driverProfile(profileRequest)
    }

    override suspend fun busRouteById(routeRequest: RouteRequest): Response<GetTravel> {
        return apiService.busRouteById(routeRequest)
    }

    override suspend fun routeStopById(stopRequest: StopRequest): Response<GetRouteStopList> {
        return apiService.routeStopById(stopRequest)
    }

}