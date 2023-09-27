package com.app.drivertracking.data.api

import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import okhttp3.ResponseBody
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

}