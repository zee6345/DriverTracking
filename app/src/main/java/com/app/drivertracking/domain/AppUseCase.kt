package com.app.drivertracking.domain

import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import com.app.drivertracking.data.models.response.success.GetRouteId
import com.app.drivertracking.data.models.response.success.GetStopsList
import com.app.drivertracking.data.remotesource.AppRepoImpl
import retrofit2.Response
import javax.inject.Inject

class AppUseCase @Inject constructor(private val appRepoImpl: AppRepoImpl) {


    suspend fun driverAuth(driverAuthRequest: DriverAuthRequest):Response<GetDriverAuth>{
        return appRepoImpl.driverAuth(driverAuthRequest)
    }

    suspend fun driverProfile(profileRequest: ProfileRequest):Response<GetDriverProfile>{
        return appRepoImpl.driverProfile(profileRequest)
    }

    suspend fun busRouteById(routeRequest: RouteRequest):Response<GetRouteId>{
        return appRepoImpl.busRouteById(routeRequest)
    }

    suspend fun routeStopById(stopRequest: StopRequest):Response<GetStopsList>{
        return appRepoImpl.routeStopById(stopRequest)
    }

}