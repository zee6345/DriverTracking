package com.app.drivertracking.presentation.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.drivertracking.R
import com.app.drivertracking.databinding.FragmentDriverMapBinding


class DriverMap : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentDriverMapBinding


    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDriverMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.mapView.getMapAsync(this)


        binding.clMap.setOnClickListener {
            navController.navigate(R.id.action_driverMap_to_driverMapDetails)
        }


    }


}