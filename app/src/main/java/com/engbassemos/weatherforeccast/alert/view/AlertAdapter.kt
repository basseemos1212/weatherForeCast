package com.engbassemos.weatherforeccast.alert.view

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
import com.engbassemos.weatherforeccast.databinding.AlertItemholderBinding
import com.engbassemos.weatherforeccast.databinding.FavItemHolderBinding
import com.engbassemos.weatherforeccast.favorite.view.FavoriteAdapter
import com.engbassemos.weatherforeccast.favorite.view.OnClickListner
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.FavouriteModel
import java.text.SimpleDateFormat
import java.util.*

class AlertAdapter (
    var list: List<AlertModel>,
    val context: Context,
    val onClickListner: OnDeleteClickListner,

    ) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {
    private lateinit var binding: AlertItemholderBinding


    inner class ViewHolder(var binding: AlertItemholderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemholderBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null) {
            holder.binding.startDate.text= formatDate(list[position].startDate,"en")
            holder.binding.endDate.text= formatDate(list[position].endDate,"en")
            holder.binding.startTime.text=formatTime(list[position].startimeOfAlert,"en")
            holder.binding.endTime.text=formatTime(list[position].endTimeOfAlert,"en")
            holder.binding.deleteButton.setOnClickListener{
                onClickListner.delete(list[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(list: List<AlertModel>) {
        this.list = list
    }
    fun formatDate(millis: Long, language: String): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val dateFormat = SimpleDateFormat("EEE d MMM", Locale(language))
        return dateFormat.format(cal.time)
    }
    fun formatTime(millis: Long, language: String): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        val dateFormat = SimpleDateFormat("h:mm a", Locale(language))
        return dateFormat.format(cal.time)
    }

}