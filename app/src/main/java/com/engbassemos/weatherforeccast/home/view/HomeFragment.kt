package com.engbassemos.weatherforeccast.home.view

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.database.ConcreteLocalSource
import com.engbassemos.weatherforeccast.databinding.FragmentHomeBinding
import com.engbassemos.weatherforeccast.home.viewModel.HomeViewModel
import com.engbassemos.weatherforeccast.home.viewModel.ViewModelFactory
import com.engbassemos.weatherforeccast.model.IconReplacment
import com.engbassemos.weatherforeccast.model.RepositoryImp
import com.engbassemos.weatherforeccast.network.ApiClient
import com.engbassemos.weatherforeccast.network.ApiState
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.util.Locale

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var homeViewModel: HomeViewModel
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var bindingHF: FragmentHomeBinding
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var hourAdapter: HourAdapter
    lateinit var dayAdapter: DayAdapter

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    var latPref="lat"
    var lonPref="lon"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        if (pref.getString("homeDestination","").equals("fav")){

            latPref="favLat"
            lonPref="favLon"
            pref.edit().putString("homeDestination","home").apply()
        }else{
            latPref="lat"
            lonPref="lon"
        }
        val latitude: Double = pref.getString(latPref, null)?.toDoubleOrNull() ?: 0.0
        val longitude: Double = pref.getString(lonPref, null)?.toDoubleOrNull() ?: 0.0
        viewModelFactory = ViewModelFactory(
            RepositoryImp.getInstance(
                ApiClient(),
                ConcreteLocalSource(requireContext())
            )
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        homeViewModel.getWeatherFromRetrofit(
            latitude,
            longitude,
            pref.getString("temp","")!!,
            pref.getString("language","en")!!,
            "a139526ef08ac514711061b2df87d9ba"
        )

        lifecycleScope.launch {
            homeViewModel.weather.collect { result ->
                when (result) {
                    is ApiState.Loading -> {


                    }
                    is ApiState.Success -> {
                        var weather = result.weather
                        binding.date.text = date(pref.getString("language","en")!!,)
                        when (pref.getString("temp","")!!) {

                            "metric" -> {
                                val formattedString="${weather.current.temp}°C"
                                binding.grade.text = formattedString
                            }
                            "imperial" -> {

                                val formattedString="${weather.current.temp}°F"
                                binding.grade.text = formattedString
                            }
                            else -> {
                                val formattedString="${weather.current.temp}°K"
                                binding.grade.text = formattedString
                            }
                        }

                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address> =
                            geocoder.getFromLocation(weather.lat, weather.lon, 1) as List<Address>
                        binding.location.text="${addresses[0].adminArea}-${addresses[0].countryName}"
                        binding.status.text=weather.current.weather[0].description
                        hourAdapter = HourAdapter(weather, requireContext(),   pref.getString("temp","")!!, pref.getString("language","en")!!,)
                        binding.recyclerView.adapter = hourAdapter

                        binding.cloudValue.text=weather.current.clouds.toString()
                        binding.pressureValue.text="${weather.current.pressure} hpa"
                        binding.windSpeedValue.text="${weather.current.wind_speed} ${pref.getString("windSpeed","m/s")}"

                        binding.humidityValue.text="${weather.current.humidity} %"

                        binding.ultraVioletValue.text="${weather.current.uvi} "

                        binding.visibltyValue.text="${weather.current.visibility} m"

                        dayAdapter = DayAdapter(weather, requireContext(),   pref.getString("temp","")!!, pref.getString("language","en")!!,)
                        binding.dayRecycleView.adapter = dayAdapter
                        IconReplacment.replaceIcons(
                            weather.current.weather[0].icon,
                            binding.imageView2
                        )


                    }
                    else -> {


                    }
                }

            }
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
fun date(lang: String): String? {
    val currentDateTime = LocalDateTime.now()
    val locale = Locale(lang)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(locale)
        .withDecimalStyle(DecimalStyle.of(locale))
    val formattedDateTime = currentDateTime.format(formatter)
    return formattedDateTime
}