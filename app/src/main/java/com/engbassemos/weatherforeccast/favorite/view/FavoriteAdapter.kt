package com.engbassemos.weatherforeccast.favorite.view

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.engbassemos.weatherforeccast.databinding.FavItemHolderBinding
import com.engbassemos.weatherforeccast.databinding.HourlyItemViewBinding
import com.engbassemos.weatherforeccast.home.view.HourAdapter
import com.engbassemos.weatherforeccast.model.FavouriteModel
import com.engbassemos.weatherforeccast.model.Hourly
import com.engbassemos.weatherforeccast.model.IconReplacment
import com.engbassemos.weatherforeccast.model.WeatherResponse
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class FavoriteAdapter(
    var list: List<FavouriteModel>,
    val context: Context,
    val onClickListner: OnClickListner,

) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    private lateinit var binding: FavItemHolderBinding


    inner class ViewHolder(var binding: FavItemHolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavItemHolderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null) {
            var favItem: FavouriteModel = list[position]
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: MutableList<Address> = geocoder.getFromLocation(
                favItem.latitude, favItem.longitude, 1
            ) as MutableList<Address>
            if (addresses.isNotEmpty()) {
                holder.binding.country.text =
                    "${addresses[0].adminArea} / ${addresses[0].countryName}"
            } else {
                Toast.makeText(context, "empty", Toast.LENGTH_SHORT).show()
            }
            holder.binding.deleteIcon.setOnClickListener { onClickListner.onDelete(favItem) }
            holder.binding.root.setOnClickListener {
                val preferences: SharedPreferences=context.getSharedPreferences("PrefFile",Context.MODE_PRIVATE)
                preferences.edit().putString("favLat", favItem.latitude.toString()).apply()
                preferences.edit().putString("favLon", favItem.longitude.toString()).apply()
                onClickListner.onClickCard(favItem)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(list: List<FavouriteModel>) {
        this.list = list
    }

}
