package com.app.drivertracking.presentation.views.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;

import com.app.bustracking.utils.Converter;
import com.app.drivertracking.R;
import com.app.drivertracking.data.cache.AppPreference;
import com.app.drivertracking.data.models.response.success.GetStopsList;
import com.app.drivertracking.data.models.response.success.Stop;
import com.app.drivertracking.databinding.FragmentDriverMapBinding;
import com.app.drivertracking.presentation.utils.Constants;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class MapDriver extends BaseFragment implements OnMapReadyCallback, PermissionsListener {

    private NavController navController;
    private FragmentDriverMapBinding binding;

    private GetStopsList stopsList;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private String SOURCE_ID = "SOURCE_ID";
    private String ICON_ID = "ICON_ID";
    private String LAYER_ID = "LAYER_ID";
    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();


    private BroadcastReceiver appTimerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("app_timer_update" == intent.getAction()) {
                long totalRunningTime = intent.getLongExtra("total_running_time", 0);

                long seconds = totalRunningTime / 1000;

                long hours = seconds / 3600;
                long remainingSeconds = seconds % 3600;
                long minutes = remainingSeconds / 60;
                long finalSeconds = remainingSeconds % 60;

                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, finalSeconds);

                // Update your UI or do any other processing with the value
                binding.tvTimer.setText(formattedTime);
            }
        }
    };

    @Override
    public void initNavigation(@NonNull NavController navController) {
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDriverMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Register the BroadcastReceiver to receive updates from the service
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(appTimerReceiver, new IntentFilter("app_timer_update"));

        Mapbox.getInstance(requireActivity(), getString(R.string.mapbox_access_token));


        binding.mapBoxView.getMapAsync(this);


        //fetch stops list
        String jsonData = AppPreference.INSTANCE.getString(Constants.BUS_STOPS.name());
        stopsList = Converter.INSTANCE.fromJson(jsonData, GetStopsList.class);

        for (Stop stop : stopsList.getStop_list()) {
            symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(stop.getLng()), Double.parseDouble(stop.getLat()))));
        }

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
            }
        });


        try {


            mapboxMap.setStyle(
                    new Style.Builder()
                            .fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41") // Add the SymbolLayer icon image to the map style
                            .withImage(
                                    ICON_ID,
                                    BitmapFactory.decodeResource(
                                            requireActivity().getResources(),
                                            com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
                                    )
                            )
                            // Adding a GeoJson source for the SymbolLayer icons.
                            .withSource(
                                    new GeoJsonSource(
                                            SOURCE_ID,
                                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)
                                    )
                            ) // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                    // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                    // the coordinate point. This is offset is not always needed and is dependent on the image
                    // that you use for the SymbolLayer icon.
//                            .withLayer(
//                                    SymbolLayer(LAYER_ID, SOURCE_ID)
//                                            .withProperties(
//                                                    iconImage(ICON_ID),
//                                                    iconAllowOverlap(true),
//                                                    iconIgnorePlacement(true)
//                                            )
//                            )
                    , style -> {

                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {

            // Enable the most basic pulsing styling by ONLY using
            // the `.pulseEnabled()` method
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(requireActivity())
                    .pulseEnabled(true)
                    .build();

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(requireActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(requireActivity(), "Please allow location permission to use this app!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(requireActivity(), "Please allow location permission to use this app!", Toast.LENGTH_LONG).show();
//            finish();
        }
    }

    @Override
    public void onDestroy() {

        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver);

        super.onDestroy();
    }
}
