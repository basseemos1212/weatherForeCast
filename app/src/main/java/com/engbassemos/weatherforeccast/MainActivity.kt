package com.engbassemos.weatherforeccast

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

const val PERMISSION_ID = 1218
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var bottomNavigationView: BottomNavigationView
    var lat: Double = 0.0
    var lon: Double = 0.0

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        val language = sharedPrefs.getString("language", "en")
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        setContentView(R.layout.activity_main)


        pref = getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        val logged = pref.getBoolean("setUpComplete", false)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        bottomNavigationView = findViewById(R.id.NavBar)
        navController = findNavController(this, R.id.nav_host)
        setupWithNavController(bottomNavigationView, navController)



        bottomNavigationView.setOnClickListener {
            if (it.id == R.id.homeFragment) {
                navController.navigate(R.id.homeFragment)
            } else if (it.id == R.id.alertFragment) {
                navController.navigate(R.id.alertFragment)
            } else if (it.id == R.id.favoriteFragment) {
                navController.navigate(R.id.favoriteFragment)
            } else if (it.id == R.id.settingsFragment) {
                navController.navigate(R.id.settingsFragment)
            }
            else if (it.id == R.id.mapFragment) {
                navController.navigate(R.id.mapFragment)
            }
        }



        if (!logged) {
            val intent = Intent(this, MapAndGps::class.java)
            startActivity(intent)
            finish()
        } else {
            val gpsLocation = pref.getString("location", "gps")
            if (gpsLocation.equals("gps")) {
                Log.i("TAG", "onCreate: iam hereee")
                getLastLocation()
            }


        }
    }
    override fun onResume() {
        super.onResume()
        if (pref.getString("location", "").equals("gps")) {
            Log.i("TAG", "onCreate: iam 2hereee")

            if (checkPermissions())
                getLastLocation()
        }

    }


    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return result
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(100000000)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                lat = mLastLocation.latitude
                Log.i("TAG", "onLocationResult:$lat ")
                pref.edit().putString("lat", lat.toString()).apply()
                lon = mLastLocation.longitude
                Log.i("TAG", "onLocationResult: $lon")
                pref.edit().putString("lon", lon.toString()).apply()

            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}