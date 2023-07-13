package com.engbassemos.weatherforeccast.map.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.database.ConcreteLocalSource
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModel
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModelFactory
import com.engbassemos.weatherforeccast.model.FavouriteModel
import com.engbassemos.weatherforeccast.model.RepositoryImp
import com.engbassemos.weatherforeccast.network.ApiClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.*

class GoogleMapFragment : Fragment() {
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    private val callback = OnMapReadyCallback { googleMap ->
        if (preferences.getString("mapDestination", "").equals("fav")) {

            googleMap.setOnMapClickListener { latLng ->
                val geocoder =
                    Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses!!.isNotEmpty()) {
                    val address = addresses[0]
                    val city = address.adminArea
                    if (!city.isNullOrEmpty()) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Confirm Location")
                        builder.setPositiveButton("Yes") { dialog, it ->
                            val lat = latLng.latitude
                            val lng = latLng.longitude



                            googleMap.addMarker(
                                MarkerOptions().position(LatLng(lat, lng)).title("Marker")
                            )
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))

                            Toast.makeText(
                                requireContext(),
                                "Location saved: $city",
                                Toast.LENGTH_SHORT
                            ).show()
                            lifecycleScope.launch {
                                favoriteViewModel.insertFav(
                                    FavouriteModel(
                                        latitude = lat,
                                        longitude = lng
                                    )
                                )
                                requireActivity().onBackPressed()
                            }


                        }
                        builder.setNegativeButton("Cancel") { dialog, which ->
                        }
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Choose specific Place",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } else {
            googleMap.setOnMapClickListener { latLng ->
                val geocoder =
                    Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses!!.isNotEmpty()) {
                    val address = addresses[0]
                    val city = address.adminArea
                    if (!city.isNullOrEmpty()) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Map Alert")
                        builder.setPositiveButton("Yes") { dialog, it ->
                            val lat = latLng.latitude
                            val lng = latLng.longitude

                            preferences.edit().putString("lat", lat.toString()).apply()
                            preferences.edit().putString("lon", lng.toString()).apply()
                            preferences.edit().putString("location", "map")
                            googleMap.addMarker(
                                MarkerOptions().position(LatLng(lat, lng)).title("Marker")
                            )
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))

                            requireActivity().onBackPressed()
                            requireActivity().recreate()
                            Toast.makeText(
                                requireContext(),
                                "Location saved: $city",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        builder.setNegativeButton("Cancel") { dialog, which ->
                        }
                        val dialog = builder.create()
                        dialog.show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Choose specific Place",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val alex = LatLng(31.20, 29.91)
        googleMap.addMarker(MarkerOptions().position(alex).title("Marker"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(alex))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = requireContext().getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        favoriteViewModelFactory = FavoriteViewModelFactory(
            RepositoryImp.getInstance(
                ApiClient(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get((FavoriteViewModel::class.java))
        editor = preferences.edit()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}