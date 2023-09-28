package com.app.drivertracking.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.DataSate
import com.app.drivertracking.domain.AppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val appUseCase: AppUseCase) : ViewModel() {

    private val _driverAuth = MutableLiveData<DataSate<Any>?>()
    val driverAuth get() = _driverAuth

    private val _driverProfile = MutableLiveData<DataSate<Any>?>()
    val driverProfile get() = _driverProfile

    private val _busRouteById = MutableLiveData<DataSate<Any>?>()
    val busRoute get() = _busRouteById

    private val _busStopById = MutableLiveData<DataSate<Any>?>()
    val busStops get() = _busStopById

    fun driverAuth(driverAuthRequest: DriverAuthRequest) {
        _driverAuth.value = DataSate.Loading

        viewModelScope.launch {
            try {
                val response = appUseCase.driverAuth(driverAuthRequest)
                if (response.isSuccessful && response.body() != null) {
                    _driverAuth.value = DataSate.Success(response.body()!!)
                } else {
                    _driverAuth.value = DataSate.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                _driverAuth.value = DataSate.Error(e.message.toString())
            }
        }
    }

    fun driverProfile(profileRequest: ProfileRequest) {
        _driverProfile.value = DataSate.Loading

        viewModelScope.launch {
            try {
                val response = appUseCase.driverProfile(profileRequest)
                if (response.isSuccessful && response.body() != null) {
                    _driverProfile.value = DataSate.Success(response.body()!!)
                } else {
                    _driverProfile.value = DataSate.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                _driverProfile.value = DataSate.Error(e.message.toString())
            }
        }
    }

    fun budRouteById(routeRequest: RouteRequest) {
        _busRouteById.value = DataSate.Loading

        viewModelScope.launch {
            try {
                val response = async { appUseCase.busRouteById(routeRequest) }.await()
                if (response.isSuccessful && response.body() != null) {
                    _busRouteById.value = DataSate.Success(response.body()!!)
                } else {
                    _busRouteById.value = DataSate.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                _busRouteById.value = DataSate.Error(e.message.toString())
            }
        }
    }

    fun routeStopById(stopRequest: StopRequest) {
        _busStopById.value = DataSate.Loading

        viewModelScope.launch {
            try {
                val response = async { appUseCase.routeStopById(stopRequest) }.await()
                if (response.isSuccessful && response.body() != null) {
                    _busStopById.value = DataSate.Success(response.body()!!)
                } else {
                    _busStopById.value = DataSate.Error(response.errorBody()!!.string())
                }
            } catch (e: Exception) {
                _busStopById.value = DataSate.Error(e.message.toString())
            }
        }
    }


}