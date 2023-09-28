package com.app.drivertracking.presentation.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.app.bustracking.utils.Converter
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.DataSate
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.data.models.response.success.GetDriverProfile
import com.app.drivertracking.data.models.response.success.GetRouteId
import com.app.drivertracking.data.models.response.success.GetStopsList
import com.app.drivertracking.databinding.FragmentHomeBinding
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Progress
import com.app.drivertracking.presentation.utils.SharedModel
import com.app.drivertracking.presentation.viewmodel.AppViewModel


private val TAG = Home::class.simpleName.toString()


class Home : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var progress: AlertDialog
    private val data: AppViewModel by viewModels()
    private val sharedModel: SharedModel by viewModels()
    private val profile = MutableLiveData<GetDriverProfile?>(null)
    private lateinit var auth: GetDriverAuth


    private val appTimerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ("app_timer_update" == intent.action) {
                val totalRunningTime = intent.getLongExtra("total_running_time", 0)

                val seconds = totalRunningTime / 1000

                val hours = seconds / 3600
                val remainingSeconds = seconds % 3600
                val minutes = remainingSeconds / 60
                val finalSeconds = remainingSeconds % 60

                val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, finalSeconds)

                // Update your UI or do any other processing with the value
                binding.tvTimer.text = formattedTime
            }
        }
    }


    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = Progress(requireActivity()).showProgress()

        // Register the BroadcastReceiver to receive updates from the service
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(appTimerReceiver, IntentFilter("app_timer_update"));


        try {
            val jsonData = AppPreference.getString(Constants.DRIVER_AUTH.name)
            if (jsonData != null) {
                auth = Converter.fromJson(jsonData, GetDriverAuth::class.java)
                data.driverProfile(ProfileRequest(auth.data.driver_phone!!))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }



        profile.observe(viewLifecycleOwner) {
            it?.apply {
                binding.tvName.text = driver_profile[0].name ?: ""
                binding.tvVehicle.text = driver_profile[0].matricule ?: ""
            }
        }


        SharedModel().appTimer.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }


        binding.tvLogout.setOnClickListener {
            AppPreference.clear()
            navController.navigate(R.id.action_home2_to_auth)
        }

        binding.rv01.setOnClickListener {

            data.budRouteById(
                RouteRequest(
                    auth.data.bus_id!!
                )
            )

        }

        data.driverProfile.observe(viewLifecycleOwner) {
            when (it) {
                is DataSate.Loading -> {
                    progress.show()
                }

                is DataSate.Error -> {
                    progress.cancel()

                    Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }

                is DataSate.Success -> {
                    progress.cancel()

                    val data = it.response as GetDriverProfile

                    val jsonData = Converter.toJson(data)
                    AppPreference.putString(Constants.DRIVER_PROFILE.name, jsonData!!)


                    profile.value = data
                }

                else -> {

                }
            }
        }

        data.busRoute.observe(viewLifecycleOwner) {
            when (it) {
                is DataSate.Loading -> {
                    progress.show()
                }

                is DataSate.Error -> {
                    progress.cancel()
                    Log.e(TAG, "onViewCreated: ${it.error}")

                    Toast.makeText(requireActivity(), "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }

                is DataSate.Success -> {
                    progress.cancel()

                    val routes = it.response as GetRouteId

                    val jsonData = Converter.toJson(routes)
                    AppPreference.putString(Constants.BUS_ROUTES.name, jsonData!!)


                    //fetch stops list
                    data.routeStopById(
                        StopRequest(
                            routes.bus_route_list.route_id
                        )
                    )


                }

                else -> {

                }
            }
        }

        data.busStops.observe(viewLifecycleOwner) {
            when (it) {
                is DataSate.Loading -> {
                    progress.show()
                }

                is DataSate.Error -> {
                    progress.cancel()
                }

                is DataSate.Success -> {
                    progress.cancel()

                    val stops = it.response as GetStopsList

                    val jsonData = Converter.toJson(stops)
                    AppPreference.putString(Constants.BUS_STOPS.name, jsonData!!)


                    if (stops.stop_list.isNotEmpty()) {
                        navController.navigate(R.id.action_home2_to_driverMap)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "no stop available right now!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                else -> {

                }
            }
        }
    }

    override fun onDestroy() {

//        data.driverProfile.removeObservers(viewLifecycleOwner)

        // Unregister the BroadcastReceiver when it's no longer needed
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver);

        super.onDestroy()
    }
}