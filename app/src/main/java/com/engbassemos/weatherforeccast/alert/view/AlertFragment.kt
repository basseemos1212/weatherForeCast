package com.engbassemos.weatherforeccast.alert.view

import MyAlertWorker
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.alert.viewModel.AlertViewModel
import com.engbassemos.weatherforeccast.alert.viewModel.AlertViewModelFactory
import com.engbassemos.weatherforeccast.database.AlertState
import com.engbassemos.weatherforeccast.database.ConcreteLocalSource
import com.engbassemos.weatherforeccast.database.FavoriteState
import com.engbassemos.weatherforeccast.databinding.AlertDialogLayoutBinding
import com.engbassemos.weatherforeccast.databinding.AlertItemholderBinding
import com.engbassemos.weatherforeccast.databinding.FragmentAlertBinding
import com.engbassemos.weatherforeccast.favorite.view.FavoriteAdapter
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModel
import com.engbassemos.weatherforeccast.favorite.viewModel.FavoriteViewModelFactory
import com.engbassemos.weatherforeccast.home.viewModel.HomeViewModel
import com.engbassemos.weatherforeccast.home.viewModel.ViewModelFactory
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.RepositoryImp
import com.engbassemos.weatherforeccast.network.ApiClient
import com.engbassemos.weatherforeccast.network.ApiState
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class AlertFragment : Fragment(), OnDeleteClickListner {
    lateinit var binding: FragmentAlertBinding
    lateinit var preferences: SharedPreferences

    lateinit var homeViewModel: HomeViewModel
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var editor: Editor
    lateinit var alertViewModel: AlertViewModel
    lateinit var alertiewModelFactory: AlertViewModelFactory
    lateinit var alertAdapter: AlertAdapter
    private lateinit var calendar: Calendar
    private var type =""
    private var alertModel: AlertModel = AlertModel(
        startDate = 0L,
        endDate = 0L,
        startimeOfAlert = 0L,
        endTimeOfAlert = 0L
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        alertiewModelFactory = AlertViewModelFactory(
            RepositoryImp.getInstance(
                ApiClient(),
                ConcreteLocalSource.getInstance(requireContext())
            )
        )
        viewModelFactory = ViewModelFactory(
            RepositoryImp.getInstance(
                ApiClient(),
                ConcreteLocalSource(requireContext())
            )
        )
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        alertAdapter = AlertAdapter(emptyList(), requireContext(), this)
        preferences = requireActivity().getSharedPreferences("PrefFile", Context.MODE_PRIVATE)
        editor = preferences.edit()
        alertViewModel =
            ViewModelProvider(this, alertiewModelFactory).get((AlertViewModel::class.java))
        binding.alertRecycle.adapter=alertAdapter
        alertViewModel.getFavsFromRoom()
        lifecycleScope.launch {
            alertViewModel.alert.collect { res ->
                when (res) {
                    is AlertState.Success -> {
                        res.alerts.let {
                            println("collecting")

                            alertAdapter.submitList(it)
                            binding.alertRecycle.adapter = alertAdapter
                        }
                    }
                    else -> {


                    }
                }
            }
        }
        binding.fab.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestOverlayPermission(this)
            }
            showAlertDialog()
        }

    }

    override fun delete(alertModel: AlertModel) {
        alertViewModel.deleteFav(alertModel)
        val worker = WorkManager.getInstance(requireContext())
        worker.cancelAllWorkByTag(alertModel.startimeOfAlert.toString())

    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()

        val alertDialogBinding = AlertDialogLayoutBinding.inflate(layoutInflater)
        alertDialog.setView(alertDialogBinding.root)

        alertDialogBinding.alertDialogRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                alertDialogBinding.alertDialogAlarmRadioButton.id -> {
                    type="alarm"
                }
                alertDialogBinding.alertDialogNotificationRadioButton.id -> {
                    type = "notification"
                }
            }
        }
        alertDialogBinding.alertDialogFromBtn.setOnClickListener {
            showDatePickerDialog(alertDialogBinding, "from")
        }

        alertDialogBinding.alertDialogToBtn.setOnClickListener {
            showDatePickerDialog(alertDialogBinding, "to")
        }
        alertDialogBinding.alertDialogSaveBtn.setOnClickListener{
            if (alertModel.startDate==0L||alertModel.endDate==0L||alertModel.startimeOfAlert==0L||alertModel.endTimeOfAlert==0L){
                Toast.makeText(context, "you should complete the to and from dates", Toast.LENGTH_SHORT).show()
            }else{
                alertViewModel.insertFav(alertModel)
                var description =""
                val latitude: Double = preferences.getString("lat", null)?.toDoubleOrNull() ?: 0.0
                val longitude: Double = preferences.getString("lon", null)?.toDoubleOrNull() ?: 0.0

                homeViewModel.getWeatherFromRetrofit(
                    latitude,
                    longitude,
                    preferences.getString("temp","")!!,
                    preferences.getString("language","en")!!,
                    "a139526ef08ac514711061b2df87d9ba"
                )
                lifecycleScope.launch {
                    homeViewModel.weather.collect { result ->
                        when (result) {
                            is ApiState.Success -> {
                                var alerts = result.weather.alerts
                                if (alerts != null) {
                                    if (alerts.isEmpty()) {
                                        description = "Weather is fine"
                                    } else {
                                        description = alerts[0].tags[0]
                                    }
                                }else{
                                    description = "Weather is fine"
                                }
                                val gson = Gson()
                                val alert = gson.toJson(alertModel)
                                val inputData = Data.Builder()
                                    .putString("pojo", alert)
                                    .putString(
                                        "Description",
                                        description
                                    ).putString(
                                        "typeOfAlert",type
                                    ).build()

                                val request: WorkRequest =
                                    OneTimeWorkRequestBuilder<MyAlertWorker>()
                                        .setInputData(inputData)
                                        .setInitialDelay(alertModel.startimeOfAlert-System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                                        .addTag(alertModel.startimeOfAlert.toString()+alertModel.id.toString())
                                        .build()

                                WorkManager.getInstance(requireContext())
                                    .enqueue(request)
                                Log.i("TAG", "displayAlertDialog: enqueue")

                            }
                            else -> {
                                Log.i("TAG", "displayAlertDialog: failed")
                            }
                        }

                    }
                }
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }
    private fun sendRequestAlarm(){

        var description =""

        lifecycleScope.launch {
            homeViewModel.weather.collect { result ->
                when (result) {
                    is ApiState.Success -> {
                        var alerts = result.weather.alerts
                        if (alerts != null) {
                            if (alerts.isEmpty()) {
                                description = "Weather is fine"
                            } else {
                                description = alerts[0].tags[0]
                            }
                        }else{
                            description = "Weather is fine"
                        }
                        val gson = Gson()
                        val alert = gson.toJson(alertModel)
                        val inputData = Data.Builder()
                            .putString("pojo", alert)
                            .putString(
                                "Description",
                                description
                            ).putString(
                                "typeOfAlert","notification"
                            ).build()

                        val request: WorkRequest =
                            OneTimeWorkRequestBuilder<MyAlertWorker>()
                                .setInputData(inputData)
                                .setInitialDelay(alertModel.startimeOfAlert-System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                                .addTag(alertModel.startimeOfAlert.toString()+alertModel.id.toString())
                                .build()

                        WorkManager.getInstance(requireContext())
                            .enqueue(request)
                        Log.i("TAG", "displayAlertDialog: enqueue")

                    }
                    else -> {
                        Log.i("TAG", "displayAlertDialog: fai;ed")

                    }
                }

            }
        }


    }
    private fun showDatePickerDialog(alertDialogBinding: AlertDialogLayoutBinding, target: String) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                val selectedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(calendar.time)

                // Set the selected date to the appropriate TextView
                if (target == "from") {
                    alertDialogBinding.alertDialogFromDateTV.text = selectedDate
                } else {
                    alertDialogBinding.alertDialogToDateTV.text = selectedDate
                }

                showTimePickerDialog(alertDialogBinding, target)
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(alertDialogBinding: AlertDialogLayoutBinding, target: String) {
        val currentTime = Calendar.getInstance()

        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            if (calendar.before(currentTime)) {
                // User selected a time before the current time, show an error or handle it accordingly
                Toast.makeText(requireContext(), "Please select a valid time", Toast.LENGTH_SHORT)
                    .show()
                if (target == "from") {
                    alertDialogBinding.alertDialogFromDateTV.text = "Start date"
                    alertDialogBinding.alertDialogFromTimeTV.text = "Start time"
                } else {
                    alertDialogBinding.alertDialogToTimeTV.text = "Start time"
                    alertDialogBinding.alertDialogToDateTV.text = "Start date"
                }
                return@OnTimeSetListener
            }

            val selectedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            if (target == "from") {
                alertDialogBinding.alertDialogFromTimeTV.text = selectedTime


                alertModel.startDate=calendar.timeInMillis
                alertModel.startimeOfAlert=calendar.timeInMillis


            } else {
                alertDialogBinding.alertDialogToTimeTV.text = selectedTime

                val fromDate = alertDialogBinding.alertDialogFromDateTV.text.toString()
                val selectedFromDate =
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).parse(fromDate)

                val currentDate = Calendar.getInstance()
                val selectedDate = Calendar.getInstance()
                selectedDate.time = selectedFromDate

                if (selectedDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)) {
                    // Selected date is today, validate the "To" time
                    val selectedDateTime = Calendar.getInstance()
                    selectedDateTime.time = selectedDate.time
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedDateTime.set(Calendar.MINUTE, minute)

                    if (selectedDateTime.before(currentTime)) {
                        // User selected a time before the current time, show an error or handle it accordingly
                        Toast.makeText(
                            requireContext(),
                            "Please select a valid time",
                            Toast.LENGTH_SHORT
                        ).show()
                        alertDialogBinding.alertDialogToTimeTV.text = "Start time"
                        alertDialogBinding.alertDialogToDateTV.text = "Start date"
                        return@OnTimeSetListener
                    }
                }


                alertModel.endDate=calendar.timeInMillis
                alertModel.endTimeOfAlert=calendar.timeInMillis


            }
        }

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            timeListener,
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.updateTime(
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE)
        )

        timePickerDialog.show()
    }


}

@RequiresApi(Build.VERSION_CODES.M)
fun requestOverlayPermission(fragment: Fragment): Boolean {
    if (!Settings.canDrawOverlays(fragment.requireContext())) {
        val builder = AlertDialog.Builder(fragment.requireContext())
        builder.setMessage("enable permission to use this feature")
        builder.setPositiveButton("ok") { _, _ ->
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${fragment.requireContext().packageName}"))
            fragment.startActivityForResult(intent, 50000)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
        return false
    } else {
        return true
    }
}