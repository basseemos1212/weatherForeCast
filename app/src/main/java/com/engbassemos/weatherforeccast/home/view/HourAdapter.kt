package com.engbassemos.weatherforeccast.home.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.engbassemos.weatherforeccast.databinding.HourlyItemViewBinding
import com.engbassemos.weatherforeccast.model.Hourly
import com.engbassemos.weatherforeccast.model.IconReplacment
import com.engbassemos.weatherforeccast.model.WeatherResponse
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HourAdapter(
    var weather: WeatherResponse,
    val context: Context,
    val unit: String, val lang: String
) : RecyclerView.Adapter<HourAdapter.ViewHolder>() {
    private lateinit var binding: HourlyItemViewBinding

    val formatter = NumberFormat.getInstance(Locale(lang))

    inner class ViewHolder(var binding: HourlyItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyItemViewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weather != null) {

            var Hour: Hourly = weather.hourly[position]
            IconReplacment.replaceIcons(Hour.weather.get(0).icon , holder.binding.icon)
            holder.binding.hour.text = getTime(Hour.dt, weather.timezone)
            val formattedNumber = formatter.format(Hour.temp.toInt())
            when (unit) {
                "metric" -> {

                    holder.binding.celisios.text = "${formattedNumber}°C"
                }
                "imperial" -> {

                    holder.binding.celisios.text = "${formattedNumber}°F"
                }
                else -> {

                    holder.binding.celisios.text = "${formattedNumber}°K"
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return weather.hourly.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(dt:Int, timeZone: String): String {
        val instant = Instant.ofEpochSecond(dt.toLong())
        val zoneId = ZoneId.of(timeZone)
        val zonedDateTime = instant.atZone(zoneId)
        val formatter = DateTimeFormatter.ofPattern("K:mm a")
        return formatter.format(zonedDateTime)
    }
}
