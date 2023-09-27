package com.app.drivertracking.app

import android.app.Application
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.mapbox.mapboxsdk.Mapbox
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DriverTracking:Application() {

    override fun onCreate() {
        super.onCreate()

        AppPreference.Preference(this)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

    }

}