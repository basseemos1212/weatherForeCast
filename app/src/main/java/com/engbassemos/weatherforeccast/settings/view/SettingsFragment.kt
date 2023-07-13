package com.engbassemos.weatherforeccast.settings.view

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.databinding.FragmentSettingsBinding
import java.util.*


class SettingsFragment : Fragment() {

lateinit var binding: FragmentSettingsBinding
lateinit var preferences: SharedPreferences
lateinit var editor: Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences=requireContext().getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        editor=preferences.edit()

        if (preferences.getString("language","") == "en") {
            binding.englishRadio.isChecked = true
        } else if (preferences.getString("language","") == "ar") {
            binding.arabicRadio.isChecked = true
        }



        if (preferences.getString("location","") == "gps") {
            binding.radioButtonGps.isChecked = true
        } else if (preferences.getString("location","") == "map") {
            binding.radioButtonMap.isChecked = true
        }



        if (preferences.getString("notification","") == "enable") {
            binding.enable.isChecked = true
        } else if (preferences.getString("notification","") == "disable") {
            binding.disable.isChecked = true
        }



        if (preferences.getString("temp","") == "standard") {
            binding.kelvinRadio.isChecked = true
        } else if (preferences.getString("temp","") == "metric") {
            binding.celsiusRadio.isChecked = true
        } else if (preferences.getString("temp","") == "imperial")  {
            binding.fahrenheitRadio.isChecked = true
        }



        binding.settingsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioButtonGps.id -> {
                    editor.putString("location","gps").apply()
                    editor.putString("homeDestination","setting").apply()
                    requireActivity().recreate()
                }
                binding.radioButtonMap.id -> {
                    editor.putString("location","map").apply()
                    editor.putString("mapDestination","settings").apply()
                  

                            val currentView=view
        if (currentView != null) {
            Navigation.findNavController(currentView).navigate(R.id.action_settingsFragment_to_googleMapFragment)
        }
                }
            }
        }
        binding.languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.englishRadio.id -> {
                    editor.putString("language", "en").apply()

                    val newConfig = Configuration(resources.configuration)
                    newConfig.setLayoutDirection(Locale("en"))
                    resources.updateConfiguration(newConfig, resources.displayMetrics)

                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                    requireActivity().recreate()
                }
                binding.arabicRadio.id -> {
                    editor.putString("language", "ar").apply()

                    val newConfig = Configuration(resources.configuration)
                    newConfig.setLayoutDirection(Locale("ar"))
                    resources.updateConfiguration(newConfig, resources.displayMetrics)

// Temporarily set the screen orientation to trigger a screen rotation
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                    requireActivity().recreate()

                }
            }
        }

        binding.tempRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.celsiusRadio.id -> {
                    editor.putString("temp","metric").apply()
                    editor.putString("windSpeed","m/s").apply()

                }
                binding.kelvinRadio.id -> {
                    editor.putString("temp","standard").apply()
                    editor.putString("windSpeed","m/s").apply()


                }
                binding.fahrenheitRadio.id -> {
                    editor.putString("temp","imperial").apply()
                    editor.putString("windSpeed","m/h").apply()

                }
            }
        }
        binding.notificationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.enable.id -> {
                    editor.putString("notification","enable").apply()
                }
                binding.disable.id -> {
                    editor.putString("notification","disable").apply()
                }

            }
        }


    }



}