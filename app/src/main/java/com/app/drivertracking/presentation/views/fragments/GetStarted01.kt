package com.app.drivertracking.presentation.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.databinding.FragmentGetStarted01Binding
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Converter

class GetStarted01 : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentGetStarted01Binding

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetStarted01Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        requireActivity().onBackPressedDispatcher.addCallback(
//            this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//
//                }
//            })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jsonData = AppPreference.getString(Constants.DRIVER_AUTH.name)
        if (jsonData.isNotEmpty()) {
            val auth = Converter.fromJson(jsonData, GetDriverAuth::class.java)
            navController.navigate(R.id.home2, Bundle(), navOptions())
        } else {

//            navController.navigate(R.id.action_getStarted03_to_auth)

        }

        binding.buttonGetStarted.setOnClickListener {
            navController.navigate(R.id.action_getStarted01_to_getStarted02, Bundle(), navOptions())

        }
    }
}