package com.app.drivertracking.presentation.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference.getString
import com.app.drivertracking.data.models.response.success.GetRouteStopList
import com.app.drivertracking.data.models.response.success.GetTravel
import com.app.drivertracking.databinding.FragmentPassengerDetailsBinding
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Converter
import com.app.drivertracking.presentation.utils.Converter.fromJson

class PassengerDetails : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentPassengerDetailsBinding

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassengerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //fetch stops list
        val jsonData = getString(Constants.BUS_STOPS.name)
        val stopsList = fromJson(
            jsonData,
            GetRouteStopList::class.java
        )

        val travelStr = getString(Constants.BUS_ROUTES.name)
        val travel = fromJson(travelStr, GetTravel::class.java)


        //

        binding.tvHeaderTitle.text = stopsList.data.route.route_title
        binding.tvTitle.text = travel.data.capacity.toString()


        binding.llHeader.setOnClickListener {
            navController.navigate(R.id.action_passengerDetails_to_routeFinish)
        }

    }


}