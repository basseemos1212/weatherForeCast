package com.engbassemos.weatherforeccast.model

import android.widget.ImageView
import com.engbassemos.weatherforeccast.R

object IconReplacment {

    const val ICON: String = "icon"

    fun replaceIcons(image: String, imageView: ImageView) {
        when (image) {
            "01d" -> imageView.setImageResource(R.drawable.clearskyday)
            "01n" -> imageView.setImageResource(R.drawable.clearskynight)

            "02d" -> imageView.setImageResource(R.drawable.fewcloudsday)
            "02n" -> imageView.setImageResource(R.drawable.fewcloudnight)

            "03d" -> imageView.setImageResource(R.drawable.scatterdcloudday)
            "03n" -> imageView.setImageResource(R.drawable.scatterdcloudnight)

            "04d" -> imageView.setImageResource(R.drawable.brokencloudsday)
            "04n" -> imageView.setImageResource(R.drawable.brokencloudsnight)

            "09d" -> imageView.setImageResource(R.drawable.rainday)
            "09n" -> imageView.setImageResource(R.drawable.rainnight)

            "10d" -> imageView.setImageResource(R.drawable.rainday)
            "10n" -> imageView.setImageResource(R.drawable.rainnight)

            "11d" -> imageView.setImageResource(R.drawable.thunderday)
            "11n" -> imageView.setImageResource(R.drawable.thundernight)

            "13d" -> imageView.setImageResource(R.drawable.snowday)
            "13n" -> imageView.setImageResource(R.drawable.snownight)

            "50d" -> imageView.setImageResource(R.drawable.mistday)
            "50n" -> imageView.setImageResource(R.drawable.mistnight)

        }
    }
}