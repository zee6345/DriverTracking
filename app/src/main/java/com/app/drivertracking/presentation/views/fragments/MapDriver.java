package com.app.drivertracking.presentation.views.fragments;

//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
//import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;

import com.app.drivertracking.R;
import com.app.drivertracking.app.LocationUpdateService;
import com.app.drivertracking.data.cache.AppPreference;
import com.app.drivertracking.data.models.response.success.GetRouteStopList;
import com.app.drivertracking.data.models.response.success.StopX;
import com.app.drivertracking.databinding.FragmentDriverMapBinding;
import com.app.drivertracking.presentation.utils.Constants;
import com.app.drivertracking.presentation.utils.Converter;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
//import com.app.drivertracking.presentation.views.activities.TurnByTurnExperienceActivity;
//import com.mapbox.android.core.permissions.PermissionsListener;
//import com.mapbox.android.core.permissions.PermissionsManager;
//import com.mapbox.api.directions.v5.MapboxDirections;
//import com.mapbox.api.directions.v5.models.DirectionsResponse;
//import com.mapbox.api.directions.v5.models.DirectionsRoute;
//import com.mapbox.geojson.Feature;
//import com.mapbox.geojson.FeatureCollection;
//import com.mapbox.geojson.LineString;
//import com.mapbox.geojson.Point;
//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.annotations.Marker;
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
//import com.mapbox.mapboxsdk.geometry.LatLng;
//import com.mapbox.mapboxsdk.geometry.LatLngBounds;
//import com.mapbox.mapboxsdk.location.LocationComponent;
//import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
//import com.mapbox.mapboxsdk.location.LocationComponentOptions;
//import com.mapbox.mapboxsdk.location.modes.CameraMode;
//import com.mapbox.mapboxsdk.location.modes.RenderMode;
//import com.mapbox.mapboxsdk.maps.MapView;
//import com.mapbox.mapboxsdk.maps.MapboxMap;
//import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
//import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.mapboxsdk.style.layers.LineLayer;
//import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
//import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
//import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//public class MapDriver extends BaseFragment implements OnMapReadyCallback, PermissionsListener {
//
//    public static FragmentDriverMapBinding _binding;
//    public static GetRouteStopList stopsList;
//    public static int proximityThreshold = 100;
//    public static int currentStopIndex = 0;
//    Context context;
//    private NavController navController;
//    private FragmentDriverMapBinding binding;
//    private PermissionsManager permissionsManager;
//    private MapboxMap mapboxMap;
//    private MapView mapView;
//    private Marker marker;
//    private String SOURCE_ID = "SOURCE_ID";
//    private String ICON_ID = "ICON_ID";
//    private String LAYER_ID = "LAYER_ID";
//    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
//    //    List<Marker> markers = new ArrayList<>();
//
//    //    double maxLat = -90;
////    double maxLng = -180;
////    double minLat = 90;
////    double minLng = 180;
//    private List<LatLng> coordinatesList = new ArrayList<>();
//    private BroadcastReceiver appTimerReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if ("app_timer_update" == intent.getAction()) {
//                long totalRunningTime = intent.getLongExtra("total_running_time", 0);
//
//                long seconds = totalRunningTime / 1000;
//
//                long hours = seconds / 3600;
//                long remainingSeconds = seconds % 3600;
//                long minutes = remainingSeconds / 60;
//                long finalSeconds = remainingSeconds % 60;
//
//                String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, finalSeconds);
//
//                // Update your UI or do any other processing with the value
//                binding.tvTimer.setText(formattedTime);
//            }
//        }
//    };
//
//    public static void checkProximityToStops(Location location) {
//        if (stopsList != null && stopsList.getData() != null) {
//            LatLng busLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//            List<StopX> stop = stopsList.getData().getStop_list();
//
//            double distanceToCurrentStop = busLatLng.distanceTo(new LatLng(Double.parseDouble(stop.get(currentStopIndex).getLat()), Double.parseDouble(stop.get(currentStopIndex).getLng())));
//
//            if (distanceToCurrentStop < proximityThreshold) {
//                // Notify the user that the bus has reached the current stop
////            showNotification("Bus has reached Stop " + (currentStopIndex + 1));
//                _binding.tvRouteTitle.setText(stop.get(currentStopIndex + 1).getStop_title());
//
//                // Move to the next stop
//                currentStopIndex++;
//
//                if (currentStopIndex < stop.size()) {
//                    // Update the route to the next stop
////                updateRoute(busLatLng, coordinatesList.get(currentStopIndex));
//
//                } else {
//                    // The bus has reached the last stop, perform any final actions
////                showNotification("Bus has reached the final stop.");
//                    _binding.tvRouteTitle.setText(stop.get(currentStopIndex - 1).getStop_title());
//                }
//            } else {
//                Log.e("mTAG", "proximity location working");
//            }
//        } else {
//            Log.e("mTAG", "data is null");
//        }
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        this.context = context;
//    }
//
//    @Override
//    public void initNavigation(@NonNull NavController navController) {
//        this.navController = navController;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        binding = FragmentDriverMapBinding.inflate(inflater, container, false);
//        _binding = binding;
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        //block rerouting
//        Home.Companion.setAlreadyRoute(true);
//
//        //
//
//        // Register the BroadcastReceiver to receive updates from the service
//        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(appTimerReceiver, new IntentFilter("app_timer_update"));
//
//        Mapbox.getInstance(requireActivity(), getString(R.string.mapbox_access_token));
//
//
//        binding.mapBoxView.getMapAsync(this);
//        mapView = binding.mapBoxView;
//        mapView.onCreate(savedInstanceState);
//
//        //fetch stops list
//        String jsonData = AppPreference.INSTANCE.getString(Constants.BUS_STOPS.name());
//        stopsList = Converter.INSTANCE.fromJson(jsonData, GetRouteStopList.class);
//
//        for (StopX stop : stopsList.getData().getStop_list()) {
//            symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(Double.parseDouble(stop.getLng()), Double.parseDouble(stop.getLat()))));
//            coordinatesList.add(new LatLng(Double.parseDouble(stop.getLat()), Double.parseDouble(stop.getLng())));
//        }
//
//
//        binding.tvDistance.setText(stopsList.getData().getRoute().getTrip_distance() + " km");
//        binding.tvRouteTitle.setText(stopsList.getData().getRoute().getRoute_title());
////        if (stopsList.getData().getRoute().getEstimated_duration().isEmpty()){
////            binding.tvEstTime.setVisibility(View.GONE);
////        } else  {
////            binding.tvEstTime.setVisibility(View.VISIBLE);
////        }
//        binding.tvEstTime.setText(stopsList.getData().getRoute().getEstimated_duration());
//
//        binding.tvRouteTitle.setOnClickListener(v -> {
//
////            navController.navigate(R.id.action_driverMap_to_driverMapDetails);
//
//
//            requireActivity().startActivity(new Intent(requireActivity(), TurnByTurnExperienceActivity.class));
//
//        });
//
//
//    }
//
//    @Override
//    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
//        this.mapboxMap = mapboxMap;
//
//        mapboxMap.setStyle(new Style.Builder().fromUri(Style.TRAFFIC_DAY), style -> {
//
//            enableLocationComponent(style);
//
////                style.addImage(ICON_ID, BitmapFactory.decodeResource(
////                        requireActivity().getResources(),
////                        com.mapbox.mapboxsdk.R.drawable.mapbox_marker_icon_default
////                ));
//
//            style.addImage(ICON_ID, requireActivity().getDrawable(R.drawable.ic_location_marker));
//
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (LatLng latLng : coordinatesList) {
//                builder.include(latLng);
//            }
//            List<Point> pointsList = new ArrayList<>();
//            for (LatLng latLng : coordinatesList) {
//                pointsList.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
//            }
//
//            // Add a GeoJson source for markers
//            style.addSource(new GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayerIconFeatureList)));
//            // Add a SymbolLayer to display markers
//            style.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
//                    .withProperties(
//                            iconImage(ICON_ID),
//                            iconAllowOverlap(false),
//                            iconIgnorePlacement(true)
//                    )
//            );
//
//            // Use Mapbox Directions API to create a route
//            MapboxDirections directionsClient = MapboxDirections.builder()
//                    .origin(Point.fromLngLat(pointsList.get(0).longitude(), pointsList.get(0).latitude()))
//                    .destination(Point.fromLngLat(pointsList.get(pointsList.size() - 1).longitude(), pointsList.get(pointsList.size() - 1).latitude()))
//                    .waypoints(pointsList.subList(1, pointsList.size() - 1))
//                    .accessToken(getString(R.string.mapbox_access_token))
//                    .overview("full")
//                    .profile("driving-traffic")
//                    .steps(true)
//                    .enableRefresh(true)
//                    .build();
//
//            // Draw the route on the map
//            directionsClient.enqueueCall(new Callback<>() {
//                @SuppressLint("MissingPermission")
//                @Override
//                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                    try {
//                        if (response.body() != null && !response.body().routes().isEmpty()) {
//                            DirectionsRoute route = response.body().routes().get(0);
//
//                            drawRouteOnMap(style, route);
////
//                            initLocationService(style);
//
//
////                            LocationComponent locationComponent = mapboxMap.getLocationComponent();
////                            locationComponent.activateLocationComponent(requireActivity(), style);
////                            locationComponent.setLocationComponentEnabled(true);
////                            locationComponent.setCameraMode(CameraMode.TRACKING);
////                            locationComponent.setRenderMode(RenderMode.NORMAL);
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//            });
//
//
//            LatLngBounds bounds = builder.build();
//
//            // Padding to control the space around the bounds (in pixels)
//            int padding = 100;
//            mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
//
//
//        });
//
//        mapboxMap.addOnMapClickListener(point -> {
//
//            LatLngBounds latLngBounds = new LatLngBounds.Builder()
//                    .include(coordinatesList.get(0)) // Northeast
//                    .include(coordinatesList.get(coordinatesList.size() - 1)) // Southwest
//                    .build();
//
//            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
//
//            return true;
//        });
//
//
//    }
//
//    private void drawRouteOnMap(@NonNull Style style, DirectionsRoute route) {
//        LineString lineString = LineString.fromPolyline(route.geometry(), 6);
//
//        List<Point> points = lineString.coordinates();
//        List<LatLng> latLngs = new ArrayList<>();
//
//        for (Point point : points) {
//            latLngs.add(new LatLng(point.latitude(), point.longitude()));
//        }
//
//        GeoJsonSource geoJsonSource = new GeoJsonSource("route-source", FeatureCollection.fromFeatures(new Feature[]{
//                Feature.fromGeometry(LineString.fromLngLats(points))
//        }));
//
//        style.addSource(geoJsonSource);
//
//        style.addLayerBelow(new LineLayer("route-layer", "route-source")
//                        .withProperties(
//                                PropertyFactory.lineColor(Color.RED),
//                                PropertyFactory.lineWidth(6f)
//                        ), LAYER_ID);
//    }
//
//    @SuppressWarnings({"MissingPermission"})
//    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {
//
//            initLocationService(loadedMapStyle);
//
//
//            Intent serviceIntent = new Intent(context, LocationUpdateService.class);
//            serviceIntent.putExtra("track", "");
//            context.startService(serviceIntent);
//
//
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(requireActivity());
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void initLocationService(Style loadedMapStyle) {
//        LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(requireActivity())
////                    .pulseEnabled(true)
////                .foregroundDrawable(R.drawable.abc)
//                .gpsDrawable(R.drawable.abc)
//                .build();
//
//        //add custom icon
////            loadedMapStyle.addImage("custom-marker-icon", BitmapFactory.decodeResource(getResources(), R.drawable.abc));
//
//        // Get an instance of the component
//        LocationComponent locationComponent = mapboxMap.getLocationComponent();
//
//        // Activate with options
//        locationComponent.activateLocationComponent(
//                LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle)
//                        .locationComponentOptions(customLocationComponentOptions)
//                        .build());
//
//        // Enable to make component visible
////        locationComponent.activateLocationComponent(requireActivity(), loadedMapStyle);
//        locationComponent.setLocationComponentEnabled(true);
//        locationComponent.setCameraMode(CameraMode.TRACKING);
//        locationComponent.setRenderMode(RenderMode.NORMAL);
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
////    @Override
////    public void onDestroyView() {
////        super.onDestroyView();
////        mapView.onDestroy();
////    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        Toast.makeText(requireActivity(), "Please allow location permission to use this app!", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//        if (granted) {
//            mapboxMap.getStyle(style -> enableLocationComponent(style));
//        } else {
//            Toast.makeText(requireActivity(), "Please allow location permission to use this app!", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//
//        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver);
//
//        super.onDestroy();
//    }
//}
