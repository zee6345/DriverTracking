package com.app.drivertracking.presentation.views.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.app.bustracking.utils.Converter
import com.app.drivertracking.R
import com.app.drivertracking.data.cache.AppPreference
import com.app.drivertracking.data.models.request.DriverAuthRequest
import com.app.drivertracking.data.models.response.DataSate
import com.app.drivertracking.data.models.response.success.GetDriverAuth
import com.app.drivertracking.databinding.FragmentAuthBinding
import com.app.drivertracking.presentation.utils.Constants
import com.app.drivertracking.presentation.utils.Progress
import com.app.drivertracking.presentation.viewmodel.AppViewModel


private val TAG = Auth::class.simpleName.toString()

class Auth : BaseFragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentAuthBinding

    private var passwordVisible = false

    private val auth: AppViewModel by viewModels()
    private lateinit var progress: AlertDialog

    override fun initNavigation(navController: NavController) {
        this.navController = navController
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress = Progress(requireActivity()).showProgress()


        binding.ivVisible.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.buttonLogin.setOnClickListener {
            val busNumber = binding.etBusNumber.text.toString()
            val numberPlate = binding.etNumberPlate.text.toString()
            val busPin = binding.etPswd.text.toString()

            if (busNumber.isNotEmpty()) {
                if (numberPlate.isNotEmpty()) {
                    if (busPin.isNotEmpty()) {

                        auth.driverAuth(DriverAuthRequest(busNumber, numberPlate, busPin))

                    } else {
                        showError()
                    }
                } else {
                    showError()
                }
            } else {
                showError()
//                binding.etBusNumber.error = ""
            }
        }

        auth.driverAuth.observe(viewLifecycleOwner) {
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

                    val data = it.response as GetDriverAuth

                    val jsonData = Converter.toJson(data)
                    AppPreference.putString(Constants.DRIVER_AUTH.name, jsonData!!)

                    navController.navigate(R.id.action_auth_to_home2)
                }

                else -> {

                }
            }
        }

    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
        if (passwordVisible) {
            binding.ivVisible.setImageResource(R.drawable.ic_visible)
            binding.etPswd.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.ivVisible.setImageResource(R.drawable.ic_invisible)
            binding.etPswd.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun showError() {
//        string.isEmpty()?:
        Toast.makeText(requireActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {

//        auth.driverAuth.removeObservers(viewLifecycleOwner)

        super.onDestroy()
    }

}