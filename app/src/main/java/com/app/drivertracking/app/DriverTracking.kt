package com.app.drivertracking.app


import android.app.Application
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.mapbox.mapboxsdk.Mapbox
//import com.mapbox.navigation.base.options.NavigationOptions
//import com.mapbox.navigation.core.MapboxNavigation
//import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class
DriverTracking : Application() {

    override fun onCreate() {
        super.onCreate()

        AppPreference.Preference(this)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))


//        val options = NavigationOptions.Builder(this)
//            .accessToken(getString(com.app.drivertracking.R.string.mapbox_access_token))
//            .build()
//
//        MapboxNavigation(options)

    }

}