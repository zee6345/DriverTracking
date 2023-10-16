package com.app.drivertracking.domain

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
import com.app.drivertracking.data.remotesource.AppRepoImpl
import retrofit2.Response
import javax.inject.Inject

class AppUseCase @Inject constructor(private val appRepoImpl: AppRepoImpl) {


    suspend fun driverAuth(driverAuthRequest: DriverAuthRequest):Response<GetDriverLogin>{
        return appRepoImpl.driverAuth(driverAuthRequest)
    }

    suspend fun driverProfile(profileRequest: ProfileRequest):Response<GetDriverProfileX>{
        return appRepoImpl.driverProfile(profileRequest)
    }

    suspend fun busRouteById(routeRequest: RouteRequest):Response<GetTravel>{
        return appRepoImpl.busRouteById(routeRequest)
    }

    suspend fun routeStopById(stopRequest: StopRequest):Response<GetRouteStopList>{
        return appRepoImpl.routeStopById(stopRequest)
    }

}