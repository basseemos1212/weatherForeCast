import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.engbassemos.weatherforeccast.R
import com.engbassemos.weatherforeccast.database.ConcreteLocalSource
import com.engbassemos.weatherforeccast.databinding.AlarmBinding
import com.engbassemos.weatherforeccast.model.AlertModel
import com.engbassemos.weatherforeccast.model.RepositoryImp
import com.engbassemos.weatherforeccast.network.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MyAlertWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    lateinit var alertModel: AlertModel

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        try {
            val description = inputData.getString("Description")
            val alertType = inputData.getString("typeOfAlert")
            val alert = inputData.getString("pojo")
            val gson = Gson()
            alertModel = gson.fromJson(alert, AlertModel::class.java)

            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis in (alertModel.startimeOfAlert + 1) until alertModel.endTimeOfAlert) {
                if (alertType == "notification") {
                    val notificationManager =
                        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel("1000", "notificationChannelName", importance)
                    notificationManager.createNotificationChannel(channel)

                    val notificationBuilder = NotificationCompat.Builder(applicationContext, "1000")
                        .setSmallIcon(R.drawable.brokencloudsday)
                        .setContentTitle(applicationContext.getString(R.string.app_name))
                        .setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    notificationManager.notify(1, notificationBuilder.build())
                } else {
                    withContext(Dispatchers.Main) {
                        fireAlarm(description!!)
                    }
                }
            }

            if (currentTimeMillis >= alertModel.endTimeOfAlert) {
                val worker = WorkManager.getInstance(applicationContext)
                worker.cancelAllWorkByTag(alertModel.startimeOfAlert.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    RepositoryImp.getInstance(ApiClient(), ConcreteLocalSource(applicationContext))
                        .deleteAlert(alertModel)
                }
            }
        } catch (e: Exception) {
            Log.e("MyAlertWorker", "Exception in doWork(): ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }

    private fun fireAlarm(desc: String) {
        val alertDialogBinding = AlarmBinding.inflate(LayoutInflater.from(applicationContext))
        val alertDialogBuilder = AlertDialog.Builder(applicationContext)

        alertDialogBuilder.setView(alertDialogBinding.root)
        alertDialogBinding.description.text = desc

        val dialog = alertDialogBuilder.create().apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            window?.setGravity(Gravity.TOP)
        }


        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.alert)
        mediaPlayer.start()

        alertDialogBinding.okBtn.setOnClickListener {
            dialog.dismiss()

            mediaPlayer.release()


            CoroutineScope(Dispatchers.Main).launch {
                RepositoryImp.getInstance(ApiClient(), ConcreteLocalSource(applicationContext))
                    .deleteAlert(alertModel)
            }

            val worker = WorkManager.getInstance(applicationContext)
            worker.cancelAllWorkByTag(alertModel.startimeOfAlert.toString())
        }

        dialog.show()

        dialog.setOnDismissListener {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis >= alertModel.endTimeOfAlert) {
                val worker = WorkManager.getInstance(applicationContext)
                worker.cancelAllWorkByTag(alertModel.startimeOfAlert.toString())
            }
        }
    }
}