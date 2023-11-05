@file:OptIn(DelicateCoroutinesApi::class)

package com.app.drivertracking.app

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.drivertracking.R
import com.app.drivertracking.data.api.ApiClient
import com.app.drivertracking.data.api.ApiService
import com.app.drivertracking.data.cache.AppPreference
import com.app.drivertracking.data.models.request.GetAddLocationResponse
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Converter
import com.app.drivertracking.presentation.utils.LocationService
import com.app.drivertracking.presentation.utils.Network
import com.app.drivertracking.presentation.views.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AppService : Service() {

    private val timerHandler = Handler(Looper.getMainLooper())
    private var totalRunningTime: Long = 0
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            totalRunningTime += 1000 // Increment by 1 second
//            Log.e("AppTimer", "App has been running for $totalRunningTime milliseconds.")

            // Broadcast the totalRunningTime value
            val broadcastIntent = Intent("app_timer_update")
            broadcastIntent.putExtra("total_running_time", totalRunningTime)
            LocalBroadcastManager.getInstance(this@AppService).sendBroadcast(broadcastIntent)

            timerHandler.postDelayed(this, 1000) // Schedule the timer to run every second
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
//    private lateinit var locationViewModel: LocationViewModel

    private val locationCallback = object : LocationCallback() {
        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            val location = locationResult.lastLocation;

            if (location != null) {


                callAPI(location)

            }

        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String? {
        val channelId = "my_service_channel_id"
        val channelName = "My Foreground Service"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        // omitted the LED color
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 20000 // 20 seconds
            fastestInterval = 20000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
                notificationManager
            ) else ""
        val notificationBuilder = NotificationCompat.Builder(this, channelId!!)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(1001, notification)

        //content
        startTimer()

        startLocationUpdates()


        val locationService = LocationService(this)
        locationService.setLocationCallback(locationCallback)

        // Check location settings and request updates if needed

        // Check location settings and request updates if needed
        locationService.checkLocationSettings(
            { location ->
                // Location settings are satisfied, and you can request location updates here
                locationService.startLocationUpdates()
            }
        ) { exception -> }
    }

    private fun startTimer() {
        timerRunnable.run()
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onDestroy() {

        stopTimer()
        stopLocationUpdates()


        super.onDestroy()
    }

    private fun startLocationUpdates() {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Handle new location updates and send them to the API
                val location = locationResult.lastLocation
                Log.e("LocationUpdate", "Lat: ${location!!.latitude}, Lng: ${location.longitude}")

                // Replace this with your API call to send the location to the server
                // Example: sendLocationToAPI(location.latitude, location.longitude)

                callAPI(location)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

    }

    @DelicateCoroutinesApi
    private fun callAPI(location: Location) {

        if (Network.isNetworkConnected(this)) {

            val apiServices = ApiClient.createService().create(ApiService::class.java)

            CoroutineScope(Dispatchers.IO).launch {

                val data = AppPreference.getString(Constants.BUS_STOPS.name)
                val jsonData = Converter.fromJson(data, GetRouteStopList::class.java)
                jsonData?.apply {
                    val busId = this.data.route.bus_id

                    val locationCall = apiServices.addlocation(
                        GetAddLocationResponse(
                            location.latitude,
                            location.longitude,
                            busId,
                        )
                    )

                    try {
                        val addLocation = async { locationCall }.await()

                        if (addLocation.isSuccessful && addLocation.body() != null) {
                            Log.e("error", "data send to api")
                        } else {
                            Log.e("error", "eerer ${addLocation.errorBody()!!.string()}")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }

            }
        } else {
            Log.e("mmTAG", "No network connection")
        }
    }


    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
    }
}