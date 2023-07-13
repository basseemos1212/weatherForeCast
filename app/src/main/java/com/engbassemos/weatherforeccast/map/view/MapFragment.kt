package com.engbassemos.weatherforeccast.map.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var map: GoogleMap
    lateinit var binding: FragmentMapBinding
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = requireContext().getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        editor = preferences.edit()

        val map =
            requireActivity().supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        map?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var myMap = LatLng(31.2001, 29.9187)
        map = googleMap
        map.setOnMapClickListener {
            myMap = LatLng(it.latitude, it.longitude)

            map.addMarker(MarkerOptions().position(myMap).title("location"))
            map.moveCamera(CameraUpdateFactory.newLatLng(myMap))
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Location Confirm")
            builder.setMessage(" " + getFullAddress(myMap.latitude, myMap.longitude) + " ?")
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                editor.putString("lat", it.latitude.toString()).apply()
                editor.putString("lon", it.longitude.toString()).apply()
            }
            builder.setNegativeButton(android.R.string.cancel) { dialog, which -> }
            builder.show()
        }

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val latLng = marker.position
        val latitude = latLng.latitude
        val longitude = latLng.longitude
        return true
    }

    private fun getFullAddress(latitude: Double, longitude: Double): Any {
        var geocoder = Geocoder(requireContext())
        var allAddress = ""
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                var city = addresses[0].locality

                var country = addresses[0].countryName

                allAddress = "$city,$country"
            }
        } catch (e: IOException) {
            Log.e("TAG", "getFullAddress: ${e.message}")
        }
        return allAddress
    }

}
