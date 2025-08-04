package com.youtubesmm.app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicReference

class IPRotationManager(private val context: Context) {
    
    private val currentIP = AtomicReference<String?>(null)
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    companion object {
        private const val IP_CHECK_URL = "https://api.ipify.org"
        private const val MAX_RETRY_ATTEMPTS = 5
        private const val AIRPLANE_MODE_DELAY = 3000L // 3 sekund
        private const val IP_CHECK_DELAY = 2000L // 2 sekund
        private const val TIMEOUT_DURATION = 30000L // 30 sekund
    }
    
    suspend fun rotateIP(): Boolean {
        return try {
            withTimeout(TIMEOUT_DURATION) {
                val oldIP = getCurrentIP()
                log("Joriy IP: $oldIP")
                
                // Samolyot rejimini yoqish
                if (!enableAirplaneMode()) {
                    log("Samolyot rejimini yoqishda xatolik")
                    return@withTimeout false
                }
                
                // 3 sekund kutish
                delay(AIRPLANE_MODE_DELAY)
                
                // Samolyot rejimini o'chirish
                if (!disableAirplaneMode()) {
                    log("Samolyot rejimini o'chirishda xatolik")
                    return@withTimeout false
                }
                
                // 2 sekund kutish
                delay(IP_CHECK_DELAY)
                
                // Yangi IP tekshirish
                val newIP = getCurrentIP()
                log("Yangi IP: $newIP")
                
                if (newIP != null && newIP != oldIP) {
                    currentIP.set(newIP)
                    log("IP muvaffaqiyatli o'zgartirildi: $oldIP -> $newIP")
                    true
                } else {
                    log("IP o'zgarmadi, qayta urinish")
                    false
                }
            }
        } catch (e: Exception) {
            log("IP rotatsiyada xatolik: ${e.message}")
            false
        }
    }
    
    suspend fun rotateIPWithRetry(): Boolean {
        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            log("IP rotatsiya urinishi ${attempt + 1}/$MAX_RETRY_ATTEMPTS")
            
            if (rotateIP()) {
                return true
            }
            
            if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                delay(5000L) // 5 sekund kutish
            }
        }
        
        log("IP rotatsiya muvaffaqiyatsiz, maksimal urinishlar soniga yetildi")
        return false
    }
    
    private suspend fun getCurrentIP(): String? {
        return try {
            val url = URL(IP_CHECK_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "GET"
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()
            
            response.trim()
        } catch (e: Exception) {
            log("IP olishda xatolik: ${e.message}")
            null
        }
    }
    
    private fun enableAirplaneMode(): Boolean {
        return try {
            // Bu ruxsat kerak: MODIFY_PHONE_STATE
            // Android 4.2+ da bu ruxsat faqat system app uchun mavjud
            // Shuning uchun Settings orqali boshqarish kerak
            log("Samolyot rejimi yoqilmoqda...")
            true // Hozircha true qaytaradi, keyin Settings orqali implement qilamiz
        } catch (e: Exception) {
            log("Samolyot rejimini yoqishda xatolik: ${e.message}")
            false
        }
    }
    
    private fun disableAirplaneMode(): Boolean {
        return try {
            log("Samolyot rejimi o'chirilmoqda...")
            true // Hozircha true qaytaradi, keyin Settings orqali implement qilamiz
        } catch (e: Exception) {
            log("Samolyot rejimini o'chirishda xatolik: ${e.message}")
            false
        }
    }
    
    fun getCurrentIPAddress(): String? = currentIP.get()
    
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    fun getNetworkType(): String {
        return when {
            isWifiConnected() -> "WiFi"
            isMobileDataConnected() -> "Mobile Data"
            else -> "No Connection"
        }
    }
    
    private fun isWifiConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
    
    private fun isMobileDataConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
    
    private fun log(message: String) {
        android.util.Log.d("IPRotationManager", message)
    }
}