package com.app.drivertracking.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel: ViewModel() {
    private val _locationResponse = MutableLiveData<String>()
    val locationResponse: LiveData<String> = _locationResponse

    fun sendLocationToAPI(latitude: Double, longitude: Double) {
        // Make your API call here to send the location data
        // Replace this with your actual API call logic

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simulate an API call
                // You can use libraries like Retrofit for actual API requests
                Thread.sleep(2000) // Simulating a network call delay
                val response = "Location sent: Lat=$latitude, Lng=$longitude"
                _locationResponse.postValue(response)
            } catch (e: Exception) {
                // Handle API call error
                _locationResponse.postValue("Error: ${e.message}")
            }
        }
    }
}