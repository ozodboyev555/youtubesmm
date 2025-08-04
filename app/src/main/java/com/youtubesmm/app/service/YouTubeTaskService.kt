package com.youtubesmm.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.webkit.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.R
import com.youtubesmm.app.data.database.AppDatabase
import com.youtubesmm.app.data.model.*
import com.youtubesmm.app.utils.IPRotationManager
import com.youtubesmm.app.utils.UserAgentManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class YouTubeTaskService : LifecycleService() {
    
    private lateinit var database: AppDatabase
    private lateinit var ipRotationManager: IPRotationManager
    private lateinit var userAgentManager: UserAgentManager
    
    private val isRunning = AtomicBoolean(false)
    private val currentTaskId = AtomicInteger(0)
    private val currentOrderId = AtomicInteger(0)
    
    private var webView: WebView? = null
    private var currentTask: Task? = null
    private var currentAccount: GoogleAccount? = null
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "youtube_smm_channel"
        private const val CHANNEL_NAME = "YouTube SMM"
        private const val CHANNEL_DESCRIPTION = "YouTube SMM ilovasi bildirishlari"
        
        private const val DELAY_BETWEEN_ACCOUNTS = 5000L // 5 sekund
        private const val DELAY_BETWEEN_TASKS = 3000L // 3 sekund
        private const val TASK_TIMEOUT = 60000L // 60 sekund
    }
    
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        ipRotationManager = IPRotationManager(this)
        userAgentManager = UserAgentManager()
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("YouTube SMM ishga tushdi"))
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
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
            lifecycleScope.launch {
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
            lifecycleScope.launch {
                processTasks()
            }
        }
    }
    
    private suspend fun processTasks() {
        while (isRunning.get()) {
            try {
                // Navbatdagi vazifani olish
                val task = database.taskDao().getNextPendingTask()
                if (task == null) {
                    log("Navbatda vazifa yo'q, 10 sekund kutish")
                    delay(10000L)
                    continue
                }
                
                currentTask.set(task)
                currentTaskId.set(task.id.toInt())
                currentOrderId.set(task.orderId.toInt())
                
                log("Vazifa boshlandi: ${task.id}, Order: ${task.orderId}")
                
                // Vazifani bajarish
                val success = executeTask(task)
                
                if (success) {
                    database.taskDao().completeTask(task.id, "COMPLETED")
                    database.orderDao().incrementCompletedCount(task.orderId)
                    log("Vazifa muvaffaqiyatli tugallandi: ${task.id}")
                } else {
                    database.taskDao().completeTask(task.id, "FAILED", errorMessage = "Vazifa bajarilmadi")
                    database.orderDao().incrementFailedCount(task.orderId)
                    log("Vazifa bajarilmadi: ${task.id}")
                }
                
                // Hisoblar orasidagi kechikish
                delay(DELAY_BETWEEN_ACCOUNTS)
                
            } catch (e: Exception) {
                log("Vazifa bajarishda xatolik: ${e.message}")
                delay(5000L)
            }
        }
    }
    
    private suspend fun executeTask(task: Task): Boolean {
        return try {
            withTimeout(TASK_TIMEOUT) {
                // IP rotatsiya
                if (!ipRotationManager.rotateIPWithRetry()) {
                    log("IP rotatsiya muvaffaqiyatsiz")
                    return@withTimeout false
                }
                
                // Navbatdagi hisobni olish
                val account = database.googleAccountDao().getNextAvailableAccount()
                if (account == null) {
                    log("Mavjud hisob yo'q")
                    return@withTimeout false
                }
                
                currentAccount.set(account)
                
                // WebView yaratish va sozlash
                setupWebView()
                
                // YouTube ga kirish
                val loginSuccess = loginToYouTube(account)
                if (!loginSuccess) {
                    log("YouTube ga kirishda xatolik")
                    database.googleAccountDao().blockAccount(account.id, "Login failed")
                    return@withTimeout false
                }
                
                // Vazifani bajarish
                val taskSuccess = performTask(task)
                if (!taskSuccess) {
                    log("Vazifa bajarishda xatolik")
                    return@withTimeout false
                }
                
                // Hisobdan chiqish
                logoutFromYouTube()
                
                // Hisobni yangilash
                database.googleAccountDao().markAccountAsUsed(account.id)
                
                // WebView ni tozalash
                clearWebView()
                
                true
            }
        } catch (e: Exception) {
            log("Vazifa bajarishda xatolik: ${e.message}")
            false
        }
    }
    
    private fun setupWebView() {
        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportMultipleWindows(true)
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccess = true
                allowContentAccess = true
                loadWithOverviewMode = true
                setRenderPriority(WebSettings.RenderPriority.HIGH)
                cacheMode = WebSettings.LOAD_NO_CACHE
                
                // User Agent o'rnatish
                userAgentString = userAgentManager.getNextUserAgent()
            }
            
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    log("Sahifa yuklandi: $url")
                }
                
                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    log("WebView xatolik: $description")
                }
            }
            
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    updateNotification("Yuklanmoqda: $newProgress%")
                }
            }
        }
    }
    
    private suspend fun loginToYouTube(account: GoogleAccount): Boolean {
        return try {
            // YouTube login sahifasiga o'tish
            webView?.loadUrl("https://accounts.google.com/signin")
            
            // Sahifa yuklanishini kutish
            delay(3000L)
            
            // Email kiritish
            val emailScript = "document.getElementById('identifierId').value = '${account.email}';"
            webView?.evaluateJavascript(emailScript, null)
            
            delay(1000L)
            
            // Keyingi tugmani bosish
            val nextButtonScript = "document.getElementById('identifierNext').click();"
            webView?.evaluateJavascript(nextButtonScript, null)
            
            delay(2000L)
            
            // Parol kiritish
            val passwordScript = "document.querySelector('input[type=\"password\"]').value = '${account.password}';"
            webView?.evaluateJavascript(passwordScript, null)
            
            delay(1000L)
            
            // Kirish tugmani bosish
            val signInButtonScript = "document.getElementById('passwordNext').click();"
            webView?.evaluateJavascript(signInButtonScript, null)
            
            delay(5000L)
            
            // YouTube ga o'tish
            webView?.loadUrl("https://www.youtube.com")
            
            delay(3000L)
            
            // Login muvaffaqiyatini tekshirish
            val checkLoginScript = "document.querySelector('#avatar-btn') !== null"
            val result = webView?.evaluateJavascript(checkLoginScript) { value ->
                log("Login natijasi: $value")
            }
            
            true // Hozircha true qaytaradi, keyin to'g'ri implement qilamiz
            
        } catch (e: Exception) {
            log("Login xatolik: ${e.message}")
            false
        }
    }
    
    private suspend fun performTask(task: Task): Boolean {
        return try {
            when (task.serviceType) {
                ServiceType.VIEWS -> performViewTask(task)
                ServiceType.LIKES -> performLikeTask(task)
                ServiceType.COMMENTS -> performCommentTask(task)
                ServiceType.SUBSCRIBERS -> performSubscribeTask(task)
                ServiceType.SHORTS_VIEWS -> performShortsViewTask(task)
                ServiceType.SHORTS_LIKES -> performShortsLikeTask(task)
                ServiceType.LIVE_PARTICIPATION -> performLiveParticipationTask(task)
            }
        } catch (e: Exception) {
            log("Vazifa bajarishda xatolik: ${e.message}")
            false
        }
    }
    
    private suspend fun performViewTask(task: Task): Boolean {
        // Video ko'rish vazifasi
        webView?.loadUrl(task.url)
        delay(10000L) // 10 sekund ko'rish
        
        // Video tugaguncha kutish
        val watchCompleteScript = "document.querySelector('.ytp-play-button').click();"
        webView?.evaluateJavascript(watchCompleteScript, null)
        
        delay(5000L)
        
        return true
    }
    
    private suspend fun performLikeTask(task: Task): Boolean {
        // Video layk vazifasi
        webView?.loadUrl(task.url)
        delay(3000L)
        
        val likeButtonScript = "document.querySelector('#top-level-buttons-computed ytd-toggle-button-renderer:first-child').click();"
        webView?.evaluateJavascript(likeButtonScript, null)
        
        delay(2000L)
        
        return true
    }
    
    private suspend fun performCommentTask(task: Task): Boolean {
        // Komment yozish vazifasi
        webView?.loadUrl(task.url)
        delay(3000L)
        
        val commentText = task.customComment ?: "Ajoyib video! 👍"
        val commentScript = """
            document.querySelector('#simplebox-placeholder').click();
            document.querySelector('#contenteditable-root').innerHTML = '$commentText';
            document.querySelector('#submit-button').click();
        """.trimIndent()
        
        webView?.evaluateJavascript(commentScript, null)
        
        delay(3000L)
        
        return true
    }
    
    private suspend fun performSubscribeTask(task: Task): Boolean {
        // Obuna bo'lish vazifasi
        webView?.loadUrl(task.url)
        delay(3000L)
        
        val subscribeScript = "document.querySelector('#subscribe-button').click();"
        webView?.evaluateJavascript(subscribeScript, null)
        
        delay(2000L)
        
        return true
    }
    
    private suspend fun performShortsViewTask(task: Task): Boolean {
        // Shorts ko'rish vazifasi
        webView?.loadUrl(task.url)
        delay(5000L) // Shorts uchun kamroq vaqt
        
        return true
    }
    
    private suspend fun performShortsLikeTask(task: Task): Boolean {
        // Shorts layk vazifasi
        webView?.loadUrl(task.url)
        delay(2000L)
        
        val shortsLikeScript = "document.querySelector('#like-button').click();"
        webView?.evaluateJavascript(shortsLikeScript, null)
        
        delay(2000L)
        
        return true
    }
    
    private suspend fun performLiveParticipationTask(task: Task): Boolean {
        // Live stream ishtirok vazifasi
        webView?.loadUrl(task.url)
        delay(5000L)
        
        // Live stream da ishtirok etish
        val liveCommentScript = """
            document.querySelector('#input-container #contenteditable-root').innerHTML = 'Salom! 👋';
            document.querySelector('#send-button').click();
        """.trimIndent()
        
        webView?.evaluateJavascript(liveCommentScript, null)
        
        delay(3000L)
        
        return true
    }
    
    private suspend fun logoutFromYouTube() {
        try {
            // YouTube dan chiqish
            webView?.loadUrl("https://accounts.google.com/Logout")
            delay(2000L)
            
            val logoutScript = "document.querySelector('#signout').click();"
            webView?.evaluateJavascript(logoutScript, null)
            
            delay(2000L)
            
        } catch (e: Exception) {
            log("Logout xatolik: ${e.message}")
        }
    }
    
    private fun clearWebView() {
        try {
            webView?.clearCache(true)
            webView?.clearHistory()
            webView?.clearFormData()
            webView?.destroy()
            webView = null
        } catch (e: Exception) {
            log("WebView tozalashda xatolik: ${e.message}")
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
    
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRunning.set(false)
        clearWebView()
    }
}