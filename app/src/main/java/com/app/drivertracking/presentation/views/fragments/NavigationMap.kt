package com.app.drivertracking.presentation.views.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.app.drivertracking.R
import com.app.drivertracking.app.LocationUpdateService
import com.app.drivertracking.data.cache.AppPreference.getString
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.databinding.NavigationMapBinding
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Converter.fromJson
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NavigationMap : BaseFragment(), OnMapReadyCallback, PermissionsListener,
    MapboxMap.OnMapClickListener {

    private lateinit var navController: NavController
    private lateinit var binding: NavigationMapBinding

    private lateinit var mapbox: MapboxMap

    //    private lateinit var btn: FloatingActionButton
    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    private var currentRoute: DirectionsRoute? = null
    private var originLocation: Location? = null

    private var navigationMapRoute: NavigationMapRoute? = null
    private var symbolManager: SymbolManager? = null

    private val coordinatesList: ArrayList<LatLng> = ArrayList()
    private val symbolLayerIconFeatureList: ArrayList<Feature> = ArrayList()
    private val pointsList: MutableList<Point> = ArrayList()

    private lateinit var stopsList: GetRouteStopList

    private val LAYER_ID = "LAYER_ID"

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Home.isAlreadyRoute = true

                navController.popBackStack()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NavigationMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //block back route
        Home.isAlreadyRoute = true

        Mapbox.getInstance(requireActivity(), getString(R.string.mapbox_access_token))
        binding.mapbox.getMapAsync(this)


        //fetch stops list
        initStopsData()


        //-----------------------------------------------
        binding.btnStartNavigation.setOnClickListener {
            if (currentRoute != null) {
                val navigationLauncherOptions = NavigationLauncherOptions.builder() //1
                    .directionsRoute(currentRoute) //2
                    .shouldSimulateRoute(false) //3
                    .build()
                NavigationLauncher.startNavigation(requireActivity(), navigationLauncherOptions) //4
            }
        }

        binding.back.setOnClickListener {
            Home.isAlreadyRoute = true
            navController.popBackStack()
        }
    }

    private fun initStopsData() {
        val jsonData = getString(Constants.BUS_STOPS.name)
        stopsList = fromJson(jsonData, GetRouteStopList::class.java)

        stopsList.data.stop_list.forEachIndexed { index, stop ->
            symbolLayerIconFeatureList.add(
                Feature.fromGeometry(
                    Point.fromLngLat(
                        stop.lng.toDouble(),
                        stop.lat.toDouble()
                    )
                )
            )
            coordinatesList.add(LatLng(stop.lat.toDouble(), stop.lng.toDouble()))
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        Log.i("Map", "is ready")
        this.mapbox = mapboxMap

        mapboxMap.setStyle(
            Style.Builder().fromUri("mapbox://styles/mapbox/streets-v11")
        ) { style ->

            //add data to points list
            coordinatesList.forEach {
                pointsList.add(Point.fromLngLat(it.longitude, it.latitude))
            }

            //add markers to map
            coordinatesList.forEach {
                addCustomMarker(it)
            }

            //enable current location
            enableLocationComponent(style)

            // Use Mapbox Directions API to create a route
//            routeWithDirectionApi(style)

            //init location
            checkLocation()

            //draw routes for navigation
            drawRouteForStops()

            //animate camera to current points
            animateCamera()

        }
    }

    private fun routeWithDirectionApi(style: Style) {
        val directionsClient: MapboxDirections = MapboxDirections.builder()
            .origin(Point.fromLngLat(pointsList[0].longitude(), pointsList[0].latitude()))
            .destination(
                Point.fromLngLat(
                    pointsList[pointsList.size - 1].longitude(),
                    pointsList[pointsList.size - 1].latitude()
                )
            )
            .accessToken(getString(R.string.mapbox_access_token))
            .overview("full")
            .profile("driving-traffic")
            .steps(true)
            .build()

        // Draw the route on the map
        directionsClient.enqueueCall(object : Callback<DirectionsResponse?> {
            override fun onResponse(
                call: Call<DirectionsResponse?>,
                response: Response<DirectionsResponse?>
            ) {
                try {
                    if (response.body() != null && response.body()!!.routes().isNotEmpty()) {
                        val route = response.body()!!.routes()[0]

                        //draw route
                        drawRouteOnMap(style, route)

                        //init location
                        checkLocation()

                        //draw routes for navigation
                        drawRouteForStops()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun drawRouteForStops() {
        originLocation?.run {
            val startPoint = Point.fromLngLat(longitude, latitude)
            val endPoint = Point.fromLngLat(
                pointsList[0].longitude(),
                pointsList[pointsList.size - 1].latitude()
            )

            getRoute(startPoint, endPoint)

        }
    }

    private fun animateCamera() {
        val builder = LatLngBounds.Builder()
        for (latLng in coordinatesList) {
            builder.include(latLng)
        }
        val bounds = builder.build()
        val padding = 100
        mapbox.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    private fun getRoute(originPoint: Point, endPoint: Point) {
        val builder = NavigationRoute.builder(requireActivity()) //1
            .accessToken(Mapbox.getAccessToken()!!) //2
            .origin(originPoint) //3
            .destination(endPoint) //4

        for (i in 1 until pointsList.size - 1) {
            builder.addWaypoint(pointsList[i])
        }

        builder.build()
            .getRoute(object : Callback<DirectionsResponse> { //6
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.d("MainActivity", t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    if (navigationMapRoute != null) {
                        navigationMapRoute?.updateRouteVisibilityTo(false)
                    } else {
                        navigationMapRoute = NavigationMapRoute(null, binding.mapbox, mapbox)
                    }

                    currentRoute = response.body()?.routes()?.first()

                    if (currentRoute != null) {
                        navigationMapRoute?.addRoute(currentRoute)
                    }

                    binding.btnStartNavigation.isEnabled = true
                }
            })
    }

    private fun drawRouteOnMap(style: Style, route: DirectionsRoute) {
        // Convert route's geometry into a LineString
        val lineString = LineString.fromPolyline(route.geometry()!!, 6)
        val sourceId = "route-source"
        val layerId = "route-layer"

        // Check if the source and layer already exist, if so, update them
        if (style.getSource(sourceId) == null) {
            style.addSource(GeoJsonSource(sourceId, Feature.fromGeometry(lineString)))
        } else {
            val source = style.getSourceAs<GeoJsonSource>(sourceId)
            source?.setGeoJson(Feature.fromGeometry(lineString))
        }

        if (style.getLayer(layerId) == null) {
            style.addLayerBelow(
                LineLayer(layerId, sourceId)
                    .withProperties(
                        PropertyFactory.lineColor(Color.RED),
                        PropertyFactory.lineWidth(5f)
                    ),
                LAYER_ID
            )
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        //ignore
    }

    override fun onPermissionResult(granted: Boolean) {
        //ignore
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireActivity())) {

            //start service to update location to server
            val locationUpdateIntent = Intent(requireActivity(), LocationUpdateService::class.java)
            locationUpdateIntent.putExtra("track", "")
            requireActivity().startService(locationUpdateIntent)


            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireActivity())
                .trackingGesturesManagement(true)
                .foregroundDrawable(R.drawable.abc)
                .accuracyColor(ContextCompat.getColor(requireActivity(), R.color.theme_color))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireActivity(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Setup map listener
            mapbox.addOnMapClickListener(this)

            // Get an instance of the LocationComponent and then adjust its settings
            mapbox.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }

            // Setup camera position
            val location = mapbox.locationComponent.lastKnownLocation
            if (location != null) {
                val position = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude))
                    .zoom(12.0) // Sets the zoom
                    .bearing(0.0) // Rotate the camera
                    .tilt(0.0) // Set the camera tilt
                    .build() // Creates a CameraPosition from the builder

                mapbox.animateCamera(
                    CameraUpdateFactory
                        .newCameraPosition(position), 1
                )
            }

            // Setup the symbol manager object
            symbolManager = SymbolManager(binding.mapbox, mapbox, loadedMapStyle)

            // add click listeners if desired
            symbolManager?.addClickListener { symbol ->

            }
            symbolManager?.addLongClickListener { symbol ->

            }
            // set non-data-driven properties, such as:
            symbolManager?.iconAllowOverlap = true
            symbolManager?.iconTranslate = arrayOf(-4f, 5f)
            symbolManager?.iconRotationAlignment = Property.ICON_ROTATION_ALIGNMENT_VIEWPORT


//            map.style?.addImage(
//                "place-marker",
//                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_location_marker)!!
//            )


        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }

    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        binding.mapbox.onStart()
        Log.d("State", "onStart()")
    }

    override fun onResume() {
        super.onResume()
        binding.mapbox.onResume()
        Log.d("State", "onResume()")
    }

    override fun onPause() {
        super.onPause()
        binding.mapbox.onPause()
        Log.d("State", "onPause()")
    }

    override fun onStop() {
        super.onStop()
        binding.mapbox.onStop()
        Log.d("State", "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapbox.onDestroy()
        Log.d("State", "onDestroy()")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapbox.onLowMemory()
        Log.d("State", "onLowMemory()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapbox.onSaveInstanceState(outState)
        Log.d("State", "onSaveInstanceState()")
    }

    private fun addPlaceMarker(location: LatLng) {
        // Add symbol at specified lat/lon
        val symbol = symbolManager?.create(
            SymbolOptions()
                .withLatLng(location)
                .withIconImage("place-marker")
                .withIconSize(1.0f)
        )
    }

    override fun onMapClick(point: LatLng): Boolean {

        addPlaceMarker(point)
        checkLocation()
        originLocation?.run {
            val startPoint = Point.fromLngLat(longitude, latitude)
            val endPoint = Point.fromLngLat(point.longitude, point.latitude)

            getRoute(startPoint, endPoint)

        }
        return false
    }

    private fun checkLocation() {
        if (originLocation == null) {
            mapbox.locationComponent.lastKnownLocation?.run {
                originLocation = this
            }
        }
    }

    private fun addCustomMarker(location: LatLng) {
        val context = requireActivity() // Get context
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_location_marker)

        drawable?.let {
            val bitmap = drawableToBitmap(it)
            val icon = IconFactory.getInstance(context).fromBitmap(bitmap)
            mapbox.addMarker(MarkerOptions().position(location).icon(icon))
        } ?: run {
            Log.e("NavigationMap", "Resource could not be decoded or is null.")
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}