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
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.app.drivertracking.data.models.BusModel
import com.app.drivertracking.data.models.request.ProfileRequest
import com.app.drivertracking.data.models.request.RouteRequest
import com.app.drivertracking.data.models.request.StopRequest
import com.app.drivertracking.data.models.response.DataSate
import com.app.drivertracking.data.models.response.success.GetDriverLogin
import com.app.drivertracking.data.models.response.success.GetDriverProfileX
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.data.models.response.success.GetTravel
import com.app.drivertracking.databinding.FragmentHomeBinding
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Converter
import com.app.drivertracking.presentation.utils.Progress
import com.app.drivertracking.presentation.utils.SharedModel
import com.app.drivertracking.presentation.viewmodel.AppViewModel
import com.app.drivertracking.presentation.views.activities.EmptyAcivity


private val TAG = Home::class.simpleName.toString()



class Home : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var progress: AlertDialog
    private val data: AppViewModel by viewModels()
    private val profile = MutableLiveData<GetDriverProfileX?>(null)

    companion object{
        var isAlreadyRoute = false
    }

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
        val loginResponse = AppPreference.getString(Constants.DRIVER_AUTH.name)
        val login = Converter.fromJson(loginResponse, GetDriverLogin::class.java)

        // Register the BroadcastReceiver to receive updates from the service
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(appTimerReceiver, IntentFilter("app_timer_update"));


        try {
            val jsonData = AppPreference.getString(Constants.DRIVER_AUTH.name)
            if (jsonData != null) {
                val auth = Converter.fromJson(jsonData, GetDriverLogin::class.java)
                auth.let {
                    data.driverProfile(
                        ProfileRequest(
                            it.data.id.toString(),
                            it.data.agency_id.toString()
                        )
                    )
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }



        profile.observe(viewLifecycleOwner) {
            it?.apply {
                binding.tvName.text = login.data.name ?: ""
                binding.tvVehicle.text = data.bus_no ?: ""
            }
        }


//        SharedModel().appTimer.observe(viewLifecycleOwner) {
//            binding.tvTimer.text = it
//        }


        binding.tvLogout.setOnClickListener {
            AppPreference.clear()
            navController.navigate(R.id.action_home2_to_auth)
        }

        binding.rv01.setOnClickListener {

            data.budRouteById(
                RouteRequest(
                    profile.value!!.data.travel_id.toString()
                )
            )

            //enable route
            isAlreadyRoute = false

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

                    val driver = it.response as GetDriverProfileX


                    val jsonData = Converter.toJson(driver)
                    AppPreference.putString(Constants.DRIVER_PROFILE.name, jsonData!!)


                    profile.value = driver
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

                    val routes = it.response as GetTravel

                    val jsonData = Converter.toJson(routes)
                    AppPreference.putString(Constants.BUS_ROUTES.name, jsonData!!)


                    //fetch stops list
                    data.routeStopById(
                        StopRequest(
                            profile.value!!.data.travel_id
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

                    val stops = it.response as GetRouteStopList

                    val jsonData = Converter.toJson(stops)
                    AppPreference.putString(Constants.BUS_STOPS.name, jsonData!!)

//                    val abc = BusModel(
//                        bus_id = stops.data.route.bus_id
//                    )

                    if (stops.data.stop_list.isNotEmpty()) {
                        if (!isAlreadyRoute) {

//                            navController.navigate(R.id.action_home2_to_driverMap)
                            navController.navigate(R.id.action_home2_to_navigationMap)

//                            startActivity(Intent(requireActivity(), EmptyAcivity::class.java))

                        }
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

    override fun onDestroyView() {

        data.driverProfile.removeObservers(viewLifecycleOwner)
        data.busRoute.removeObservers(viewLifecycleOwner)
        data.busStops.removeObservers(viewLifecycleOwner)

        super.onDestroyView()
    }

    override fun onDestroy() {

        // Unregister the BroadcastReceiver when it's no longer needed
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver);

        super.onDestroy()
    }
}