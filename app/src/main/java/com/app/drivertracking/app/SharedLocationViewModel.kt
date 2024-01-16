package com.app.drivertracking.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedLocationViewModel : ViewModel()  {
    val locationResponse = MutableLiveData<String>()
}