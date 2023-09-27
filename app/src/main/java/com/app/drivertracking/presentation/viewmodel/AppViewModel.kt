package com.app.drivertracking.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.response.DataSate
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.domain.AppUseCase
import com.app.drivertracking.presentation.model.DriverDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val appUseCase: AppUseCase) : ViewModel() {

    private val _details = MutableLiveData<List<DriverDetail>>()
    val details: LiveData<List<DriverDetail>> get() = _details

    private val _driverAuth = MutableLiveData<DataSate<Any>?>()
    val driverAuth get() = _driverAuth

    private val _driverProfile = MutableLiveData<DataSate<Any>?>()
    val driverProfile get() = _driverProfile


    fun getDetails() {
        val data: ArrayList<DriverDetail> = ArrayList()

        data.add(DriverDetail("17:20", "La Chapelaine", "Arrivee prevue 17:26"))
        data.add(DriverDetail("17:20", "Port Eaguad", "Arrivee prevue 17:26"))
        data.add(DriverDetail("17:20", "La Chapelaine", "Arrivee prevue 17:26"))

        _details.postValue(data)
    }


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


}