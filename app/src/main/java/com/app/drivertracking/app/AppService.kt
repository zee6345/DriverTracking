package com.app.drivertracking.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.drivertracking.R
import com.app.drivertracking.presentation.utils.SharedModel
import com.app.drivertracking.presentation.views.activities.MainActivity


class AppService : Service() {

    private val timerHandler = Handler(Looper.getMainLooper())
    private var totalRunningTime: Long = 0
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            totalRunningTime += 1000 // Increment by 1 second
//            Log.e("AppTimer", "App has been running for $totalRunningTime milliseconds.")

            // Broadcast the totalRunningTime value
            val broadcastIntent = Intent("app_timer_update")
            broadcastIntent.putExtra("total_running_time", totalRunningTime)
            LocalBroadcastManager.getInstance(this@AppService).sendBroadcast(broadcastIntent)

            timerHandler.postDelayed(this, 1000) // Schedule the timer to run every second
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String? {
        val channelId = "my_service_channel_id"
        val channelName = "My Foreground Service"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        // omitted the LED color
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onCreate() {
        super.onCreate()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
                notificationManager
            ) else ""
        val notificationBuilder = NotificationCompat.Builder(this, channelId!!)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(1001, notification)

        //content
        startTimer()

    }

    private fun startTimer() {
        timerRunnable.run()
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onDestroy() {

        stopTimer()

        super.onDestroy()
    }
}