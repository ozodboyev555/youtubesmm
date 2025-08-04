package com.youtubesmm.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.webkit.*
import androidx.core.app.NotificationCompat
import com.youtubesmm.app.R
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class YouTubeTaskService : Service() {

    private val isRunning = AtomicBoolean(false)
    private val currentTaskId = AtomicInteger(0)
    private val currentOrderId = AtomicInteger(0)

    private var webView: WebView? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "youtube_smm_channel"
        private const val CHANNEL_NAME = "YouTube SMM"
        private const val CHANNEL_DESCRIPTION = "YouTube SMM ilovasi bildirishlari"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("YouTube SMM ishga tushdi"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START_SERVICE" -> startService()
            "STOP_SERVICE" -> stopService()
            "PAUSE_SERVICE" -> pauseService()
            "RESUME_SERVICE" -> resumeService()
        }
        return START_STICKY
    }

    private fun startService() {
        if (isRunning.compareAndSet(false, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                processTasks()
            }
        }
    }

    private fun stopService() {
        isRunning.set(false)
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService() {
        isRunning.set(false)
        updateNotification("YouTube SMM pauzada")
    }

    private fun resumeService() {
        if (isRunning.compareAndSet(false, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                processTasks()
            }
        }
    }

    private suspend fun processTasks() {
        while (isRunning.get()) {
            try {
                delay(10000L) // 10 sekund kutish
                log("Vazifa jarayonda...")
            } catch (e: Exception) {
                log("Vazifa bajarishda xatolik: ${e.message}")
                delay(5000L)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("YouTube SMM")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(message: String) {
        val notification = createNotification(message)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun log(message: String) {
        android.util.Log.d("YouTubeTaskService", message)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning.set(false)
        webView?.destroy()
    }
}