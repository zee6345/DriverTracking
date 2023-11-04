package com.app.drivertracking.presentation.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference.getString
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.databinding.FragmentDriverMapDetailsBinding
import com.app.drivertracking.presentation.ui.DetailAdapter
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Converter.fromJson
import com.app.drivertracking.presentation.viewmodel.AppViewModel


class DriverMapDetails : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentDriverMapDetailsBinding
    private val dataSource: AppViewModel by viewModels()

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
        binding = FragmentDriverMapDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register the BroadcastReceiver to receive updates from the service
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(appTimerReceiver, IntentFilter("app_timer_update"));

        //fetch stops list
        val jsonData = getString(Constants.BUS_STOPS.name)
        val stopsList = fromJson(
            jsonData,
            GetRouteStopList::class.java
        )

        binding.tvDistance.text = stopsList.data.route.trip_distance + " Km"
        binding.tvEstTime.text = stopsList.data.route.estimated_duration ?: ""
        binding.tvRouteTitle.text = stopsList.data.route.route_title ?: ""

        val detailAdapter = DetailAdapter(stopsList.data.stop_list) {
            navController.navigate(R.id.action_driverMapDetails_to_passengerDetails)
        }

        binding.rvDetails.setHasFixedSize(true)
        binding.rvDetails.adapter = detailAdapter


    }

    override fun onDestroy() {

        // Unregister the BroadcastReceiver when it's no longer needed
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(appTimerReceiver);

        super.onDestroy()
    }


}