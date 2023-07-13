package com.engbassemos.weatherforeccast.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engbassemos.weatherforeccast.databinding.DaysWeatherItemviewBinding
import com.engbassemos.weatherforeccast.model.Daily
import com.engbassemos.weatherforeccast.model.IconReplacment
import com.engbassemos.weatherforeccast.model.WeatherResponse
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DayAdapter(
    var weather: WeatherResponse,
    val context: Context,
    val unit: String,
    val lang: String
) : RecyclerView.Adapter<DayAdapter.ViewHolder>() {
    private lateinit var binding: DaysWeatherItemviewBinding
    val formatter = NumberFormat.getInstance(Locale(lang))

    inner class ViewHolder(var binding: DaysWeatherItemviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DaysWeatherItemviewBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weather != null) {
            var Day: Daily = weather.daily[position]
            IconReplacment.replaceIcons(Day.weather.get(0).icon , holder.binding.weekIcon)
            holder.binding.day.text = getDay(Day.dt,lang)
            holder.binding.weatherStatus.text = Day.weather.get(0).description
            val formattedNumber1 = formatter.format(Day.temp.min.toInt())
            val formattedNumber2 = formatter.format(Day.temp.max.toInt())
            when (unit) {
                "metric" -> {

                    holder.binding.weekCelisios.text = "${formattedNumber1}/${formattedNumber2}°C"
                }
                "imperial" -> {

                    holder.binding.weekCelisios.text = "${formattedNumber1}/${formattedNumber2}°F"
                }
                else -> {

                    holder.binding.weekCelisios.text = "${formattedNumber1}/${formattedNumber2}°K"
                }
            }


        }
    }

    override fun getItemCount(): Int {
        return weather.daily.size
    }
    fun getDay(dt: Int,lang:String): String {
        val format = SimpleDateFormat("E",Locale(lang))
        val date = Date(dt.toLong() * 1000)
        return format.format(date)
    }

}