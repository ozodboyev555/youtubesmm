package com.youtubesmm.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youtubesmm.app.data.database.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel : ViewModel() {
    
    private val database = AppDatabase.getInstance()
    
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                database.orderDao().getActiveOrders(),
                database.googleAccountDao().getActiveAccounts()
            ) { orders, accounts ->
                val activeOrders = orders.size
                val totalAccounts = accounts.size
                val availableAccounts = accounts.count { it.isActive && !it.isBlocked }
                
                // Bugun tugallangan vazifalarni hisoblash
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val completedToday = orders.sumOf { it.completedCount }
                
                // Progress hisoblash
                val totalTasks = orders.sumOf { it.quantity }
                val completedTasks = orders.sumOf { it.completedCount }
                val progressPercentage = if (totalTasks > 0) {
                    (completedTasks.toFloat() / totalTasks.toFloat()) * 100
                } else 0f
                
                // Status aniqlash
                val status = when {
                    activeOrders == 0 -> "Ish yo'q"
                    completedTasks == totalTasks -> "Tugallandi"
                    else -> "Ish jarayonda"
                }
                
                val statusColor = when (status) {
                    "Ish yo'q" -> android.graphics.Color.GRAY
                    "Tugallandi" -> android.graphics.Color.GREEN
                    else -> android.graphics.Color.BLUE
                }
                
                HomeState(
                    status = status,
                    statusColor = statusColor,
                    activeOrders = activeOrders,
                    completedToday = completedToday,
                    totalAccounts = totalAccounts,
                    availableAccounts = availableAccounts,
                    progressPercentage = progressPercentage,
                    isWorking = activeOrders > 0 && completedTasks < totalTasks,
                    isLoading = false
                )
            }.collect { state ->
                _homeState.value = state
            }
        }
    }
    
    fun refreshData() {
        loadHomeData()
    }
}

data class HomeState(
    val status: String = "Yuklanmoqda...",
    val statusColor: Int = android.graphics.Color.GRAY,
    val activeOrders: Int = 0,
    val completedToday: Int = 0,
    val totalAccounts: Int = 0,
    val availableAccounts: Int = 0,
    val progressPercentage: Float = 0f,
    val isWorking: Boolean = false,
    val isLoading: Boolean = true
)