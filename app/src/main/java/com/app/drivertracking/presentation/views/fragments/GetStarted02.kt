package com.app.drivertracking.presentation.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.app.drivertracking.R
import com.app.drivertracking.databinding.FragmentGetStarted02Binding

class GetStarted02 : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding:FragmentGetStarted02Binding

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGetStarted02Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGetStarted.setOnClickListener {
            navController.navigate(R.id.action_getStarted02_to_getStarted03)
        }
    }
}