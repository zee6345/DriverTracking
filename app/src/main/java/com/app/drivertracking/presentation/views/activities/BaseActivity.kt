package com.app.drivertracking.presentation.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.drivertracking.app.AppService
import com.app.drivertracking.app.LocationUpdateService
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity()
//    ,ConnectionEventListener,
//    SubscriptionEventListener
{

//    val PUSHER_APP_ID = 1695142
//    val PUSHER_APP_KEY = "1f3eac61c1534d7ca731"
//    val PUSHER_APP_SECRET = "820dcb7d16632e710184"
//
//    val pusherOptions by lazy {
//        PusherOptions().setCluster("ap2")
//    }
//    val pusher by lazy {
//        Pusher(PUSHER_APP_KEY, pusherOptions)
//    }

    val REQUEST_ENABLE_LOCATION = 100012

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else{
            enableLocation()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(Intent(this, AppService::class.java))
//        } else {
//            startService(Intent(this, AppService::class.java))
//        }


//        pusher.connect(this, ConnectionState.ALL)
////        val channel = pusher.subscribe("220.location")
//        val channel = pusher.subscribe("seentul-tracking")
//        channel.bind("220location", this)



    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1010
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1010) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                getLocation()
                enableLocation()
            }
        } else {
            Toast.makeText(
                this,
                "Please allow location permission to use this app!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

//    override fun onConnectionStateChange(change: ConnectionStateChange?) {
//        Log.e("Pusher", "State changed from " + change!!.getPreviousState() + " to " + change!!.getCurrentState())
//    }
//
//    override fun onError(message: String?, code: String?, e: Exception?) {
//        Log.e("Pusher", "There was a problem connecting! " +
//                "\ncode: " + code +
//                "\nmessage: " + message +
//                "\nException: " + e
//        )
//    }
//
//    override fun onEvent(event: PusherEvent?) {
//        Log.e("Pusher", "Received event with data: " + event.toString());
//    }

    fun enableLocation(){
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location settings are satisfied, you can enable location services here
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but can be resolved by showing the user a dialog.
                try {
                    exception.startResolutionForResult(this, REQUEST_ENABLE_LOCATION)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore or handle the error
                }
            }
        }

    }

    override fun onDestroy() {

        //destroy service when app closed
        val locationUpdateServices = Intent(this@BaseActivity, LocationUpdateService::class.java)
        stopService(locationUpdateServices)

        super.onDestroy()
    }


}