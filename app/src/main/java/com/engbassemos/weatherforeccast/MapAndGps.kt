package com.engbassemos.weatherforeccast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.engbassemos.weatherforeccast.databinding.ActivityMapAndGpsBinding

class MapAndGps : AppCompatActivity() {
    var setUpComplete = false;
    lateinit var binding: ActivityMapAndGpsBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapAndGpsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("TAG", "onCreate:initial set up")
        val pref = getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        binding.switchNoti.isChecked = true
        Log.i("TAG", "onCreate:initial set up after switch notifi ")

        binding.btnSetUpDone.setOnClickListener {
            pref.edit().putBoolean("setUpComplete", true).apply()
            setUpComplete = true
            if (binding.rbgps.isChecked) {
                pref.edit().putString("location", "gps").apply()
                Log.i("TAG", "onCreate:initial set up location is gps ")
            } else if (binding.rbMap.isChecked) {
                pref.edit().putString("location", "map").apply()
            }

            binding.switchNoti.setOnClickListener {
                if (binding.switchNoti.isChecked) {
                    pref.edit().putBoolean("noti", true).apply()

                } else {
                    pref.edit().putBoolean("noti", false).apply()

                }
            }
            val locationPref = pref.getString("location", "")
            if (locationPref == "gps") {
                Log.i("TAG", "onCreate: $locationPref")
                val intent = Intent(this, MainActivity::class.java)
                Log.i("TAG", "onCreate:  initial set up go to home activity")
                startActivity(intent)
                finish()
            } else if (locationPref == "map") {
                val intent = Intent(this, MainActivity::class.java)
                Log.i("TAG", "onCreate:  initial set up go to home activity")
                startActivity(intent)
                finish()
            }


        }
        Log.i("TAG", "onCreate:initial set up after set on click listener")

    }
}

