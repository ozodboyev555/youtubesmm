package com.youtubesmm.app.ui.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MonitoringViewModel : ViewModel() {

    private val _monitoringState = MutableStateFlow(MonitoringState())
    val monitoringState: StateFlow<MonitoringState> = _monitoringState

    init {
        loadMonitoringData()
    }

    private fun loadMonitoringData() {
        viewModelScope.launch {
            // Simplified data for now
            val state = MonitoringState(
                status = "Monitoring",
                statusColor = android.graphics.Color.BLUE,
                progressPercentage = 0f,
                totalTasks = 0,
                completedTasks = 0,
                failedTasks = 0,
                remainingTasks = 0,
                estimatedTimeRemaining = null,
                startTime = null,
                endTime = null,
                isLoading = false
            )
            _monitoringState.value = state
        }
    }

    fun refreshData() {
        loadMonitoringData()
    }

    fun clearHistory() {
        viewModelScope.launch {
            _monitoringState.value = _monitoringState.value.copy(
                totalTasks = 0,
                completedTasks = 0,
                failedTasks = 0,
                remainingTasks = 0,
                progressPercentage = 0f
            )
        }
    }
}

data class MonitoringState(
    val status: String = "Monitoring",
    val statusColor: Int = android.graphics.Color.BLUE,
    val progressPercentage: Float = 0f,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val failedTasks: Int = 0,
    val remainingTasks: Int = 0,
    val estimatedTimeRemaining: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val isLoading: Boolean = false
)