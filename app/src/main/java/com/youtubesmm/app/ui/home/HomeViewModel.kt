package com.youtubesmm.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Simplified data for now
            val state = HomeState(
                status = "Tayyor",
                statusColor = android.graphics.Color.GREEN,
                activeOrders = 0,
                completedToday = 0,
                totalAccounts = 0,
                availableAccounts = 0,
                progressPercentage = 0f,
                isWorking = false,
                isLoading = false
            )
            _homeState.value = state
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