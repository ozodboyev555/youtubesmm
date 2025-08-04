package com.youtubesmm.app.utils

import java.util.concurrent.atomic.AtomicInteger

class UserAgentManager {
    
    private val currentIndex = AtomicInteger(0)
    
    companion object {
        private val USER_AGENTS = listOf(
            // Chrome Mobile User Agents
            "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 12; OnePlus 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 13; Samsung Galaxy S22) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 14; Google Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.74 Mobile Safari/537.36",
            
            // Firefox Mobile User Agents
            "Mozilla/5.0 (Android 10; Mobile; rv:91.0) Gecko/91.0 Firefox/91.0",
            "Mozilla/5.0 (Android 11; Mobile; rv:92.0) Gecko/92.0 Firefox/92.0",
            "Mozilla/5.0 (Android 12; Mobile; rv:93.0) Gecko/93.0 Firefox/93.0",
            "Mozilla/5.0 (Android 13; Mobile; rv:94.0) Gecko/94.0 Firefox/94.0",
            "Mozilla/5.0 (Android 14; Mobile; rv:95.0) Gecko/95.0 Firefox/95.0",
            
            // Samsung Internet User Agents
            "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/14.2 Chrome/87.0.4280.141 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/15.0 Chrome/89.0.4389.105 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 12; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/16.0 Chrome/91.0.4472.120 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 13; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/17.0 Chrome/92.0.4515.131 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 14; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/18.0 Chrome/93.0.4577.82 Mobile Safari/537.36",
            
            // Opera Mobile User Agents
            "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36 OPR/62.0.2254.60986",
            "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36 OPR/63.0.3216.58473",
            "Mozilla/5.0 (Linux; Android 12; OnePlus 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Mobile Safari/537.36 OPR/64.0.3282.53912",
            "Mozilla/5.0 (Linux; Android 13; Samsung Galaxy S22) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Mobile Safari/537.36 OPR/65.0.3325.10947",
            "Mozilla/5.0 (Linux; Android 14; Google Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.74 Mobile Safari/537.36 OPR/66.0.3359.60326",
            
            // Edge Mobile User Agents
            "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36 EdgA/91.0.864.59",
            "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36 EdgA/92.0.902.55",
            "Mozilla/5.0 (Linux; Android 12; OnePlus 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Mobile Safari/537.36 EdgA/93.0.961.47",
            "Mozilla/5.0 (Linux; Android 13; Samsung Galaxy S22) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Mobile Safari/537.36 EdgA/94.0.992.31",
            "Mozilla/5.0 (Linux; Android 14; Google Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.74 Mobile Safari/537.36 EdgA/95.0.1020.30",
            
            // UC Browser User Agents
            "Mozilla/5.0 (Linux; U; Android 10; en-US; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/91.0.4472.120 UCBrowser/13.4.0.1306 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; U; Android 11; en-US; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/92.0.4515.131 UCBrowser/13.4.0.1306 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; U; Android 12; en-US; OnePlus 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/93.0.4577.82 UCBrowser/13.4.0.1306 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; U; Android 13; en-US; Samsung Galaxy S22) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/94.0.4606.81 UCBrowser/13.4.0.1306 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; U; Android 14; en-US; Google Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/95.0.4638.74 UCBrowser/13.4.0.1306 Mobile Safari/537.36"
        )
    }
    
    fun getNextUserAgent(): String {
        val index = currentIndex.getAndIncrement() % USER_AGENTS.size
        return USER_AGENTS[index]
    }
    
    fun getRandomUserAgent(): String {
        return USER_AGENTS.random()
    }
    
    fun getUserAgentByIndex(index: Int): String {
        return USER_AGENTS[index % USER_AGENTS.size]
    }
    
    fun getTotalUserAgentCount(): Int = USER_AGENTS.size
    
    fun getCurrentIndex(): Int = currentIndex.get()
    
    fun resetIndex() {
        currentIndex.set(0)
    }
    
    fun getUserAgentInfo(userAgent: String): UserAgentInfo {
        return when {
            userAgent.contains("Chrome") && !userAgent.contains("SamsungBrowser") && !userAgent.contains("OPR") && !userAgent.contains("EdgA") -> {
                UserAgentInfo("Chrome", "Google Chrome Mobile")
            }
            userAgent.contains("Firefox") -> {
                UserAgentInfo("Firefox", "Mozilla Firefox Mobile")
            }
            userAgent.contains("SamsungBrowser") -> {
                UserAgentInfo("Samsung Internet", "Samsung Internet Browser")
            }
            userAgent.contains("OPR") -> {
                UserAgentInfo("Opera", "Opera Mobile")
            }
            userAgent.contains("EdgA") -> {
                UserAgentInfo("Edge", "Microsoft Edge Mobile")
            }
            userAgent.contains("UCBrowser") -> {
                UserAgentInfo("UC Browser", "UC Browser Mobile")
            }
            else -> {
                UserAgentInfo("Unknown", "Unknown Browser")
            }
        }
    }
    
    data class UserAgentInfo(
        val browserName: String,
        val fullName: String
    )
}