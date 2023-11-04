package com.app.drivertracking.presentation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationService {
    private final Context context;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public LocationService(Context context) {
        this.context = context;
        initializeLocationRequest();
    }

    private void initializeLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(50000); // Update interval in milliseconds
        locationRequest.setFastestInterval(50000); // Fastest update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void setLocationCallback(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(context)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        LocationServices.getFusedLocationProviderClient(context)
                .removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    public Task<Location> getLastLocation() {
        return LocationServices.getFusedLocationProviderClient(context).getLastLocation();
    }

    public void checkLocationSettings(OnSuccessListener<Location> onSuccessListener, OnFailureListener onFailureListener) {
        Task<Location> lastLocationTask = getLastLocation();
        lastLocationTask.addOnSuccessListener(onSuccessListener);
        lastLocationTask.addOnFailureListener(onFailureListener);
    }
}
