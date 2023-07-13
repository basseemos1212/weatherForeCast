package com.engbassemos.weatherforeccast.alert.view

import com.engbassemos.weatherforeccast.model.AlertModel

interface OnDeleteClickListner {
fun delete(alertModel: AlertModel)
}