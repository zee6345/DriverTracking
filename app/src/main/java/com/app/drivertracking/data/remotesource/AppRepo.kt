package com.app.drivertracking.data.remotesource

import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import retrofit2.Response

interface AppRepo {

    suspend fun driverAuth(driverAuthRequest: DriverAuthRequest):Response<GetDriverAuth>

    suspend fun driverProfile(profileRequest: ProfileRequest):Response<GetDriverProfile>

}