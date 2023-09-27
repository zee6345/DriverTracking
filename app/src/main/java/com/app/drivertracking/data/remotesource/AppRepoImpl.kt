package com.app.drivertracking.data.remotesource


import com.app.drivertracking.data.api.ApiService
import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import retrofit2.Response

import javax.inject.Inject

class AppRepoImpl @Inject constructor(private val apiService: ApiService) : AppRepo {

    override suspend fun driverAuth(driverAuthRequest: DriverAuthRequest): Response<GetDriverAuth> {
        return apiService.driverLogin(driverAuthRequest)
    }

    override suspend fun driverProfile(profileRequest: ProfileRequest): Response<GetDriverProfile> {
        return apiService.driverProfile(profileRequest)
    }

}