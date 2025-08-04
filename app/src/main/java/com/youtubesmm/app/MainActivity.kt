package com.youtubesmm.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.youtubesmm.app.databinding.ActivityMainBinding
import com.youtubesmm.app.service.YouTubeTaskService

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "Barcha ruxsatlar berildi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ba'zi ruxsatlar rad etildi", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
        checkAndRequestPermissions()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)
        
        // Navigation item click listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_order -> {
                    navController.navigate(R.id.orderFragment)
                    true
                }
                R.id.nav_worker -> {
                    navController.navigate(R.id.workerFragment)
                    true
                }
                R.id.nav_monitoring -> {
                    navController.navigate(R.id.monitoringFragment)
                    true
                }
                R.id.nav_accounts -> {
                    navController.navigate(R.id.accountsFragment)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        // Internet ruxsati
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE)
        }
        
        // Fayl o'qish/yozish ruxsati
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        // Android 13+ uchun media ruxsati
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
        
        // Orqa fonda ishlash ruxsati
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WAKE_LOCK)
        }
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        
        // System Alert Window ruxsati
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
        }
        
        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }
    
    fun startYouTubeService() {
        val intent = Intent(this, YouTubeTaskService::class.java).apply {
            action = "START_SERVICE"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    
    fun stopYouTubeService() {
        val intent = Intent(this, YouTubeTaskService::class.java).apply {
            action = "STOP_SERVICE"
        }
        startService(intent)
    }
    
    fun pauseYouTubeService() {
        val intent = Intent(this, YouTubeTaskService::class.java).apply {
            action = "PAUSE_SERVICE"
        }
        startService(intent)
    }
    
    fun resumeYouTubeService() {
        val intent = Intent(this, YouTubeTaskService::class.java).apply {
            action = "RESUME_SERVICE"
        }
        startService(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Ilova yopilganda service ni to'xtatish
        stopYouTubeService()
    }
}