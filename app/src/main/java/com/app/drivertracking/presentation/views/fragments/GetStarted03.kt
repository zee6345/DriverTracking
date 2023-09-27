package com.app.drivertracking.presentation.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.bustracking.utils.Converter
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.databinding.FragmentGetStarted03Binding

class GetStarted03 : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentGetStarted03Binding

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetStarted03Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.buttonGetStarted.setOnClickListener {


            //if already login move to home
            val jsonData = AppPreference.getString("auth")
            if (jsonData.isNotEmpty()) {

                val auth = Converter.fromJson(jsonData, GetDriverAuth::class.java)
                navController.navigate(R.id.home2)

            } else {

                navController.navigate(R.id.action_getStarted03_to_auth)

            }


        }

    }
}