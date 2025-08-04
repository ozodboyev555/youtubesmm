package com.youtubesmm.app.ui.worker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkerViewModel : ViewModel() {

    private val _workerState = MutableStateFlow(WorkerState())
    val workerState: StateFlow<WorkerState> = _workerState

    fun startWork() {
        viewModelScope.launch {
            _workerState.value = _workerState.value.copy(
                isWorking = true,
                isPaused = false,
                status = "Ish boshlandi",
                statusColor = android.graphics.Color.GREEN
            )
        }
    }

    fun stopWork() {
        viewModelScope.launch {
            _workerState.value = _workerState.value.copy(
                isWorking = false,
                isPaused = false,
                status = "Ish to'xtatildi",
                statusColor = android.graphics.Color.RED
            )
        }
    }

    fun pauseWork() {
        viewModelScope.launch {
            _workerState.value = _workerState.value.copy(
                isPaused = true,
                status = "Ish pauzada",
                statusColor = android.graphics.Color.parseColor("#FF9800")
            )
        }
    }

    fun resumeWork() {
        viewModelScope.launch {
            _workerState.value = _workerState.value.copy(
                isPaused = false,
                status = "Ish davom etmoqda",
                statusColor = android.graphics.Color.GREEN
            )
        }
    }
}

data class WorkerState(
    val isWorking: Boolean = false,
    val isPaused: Boolean = false,
    val status: String = "Tayyor",
    val statusColor: Int = android.graphics.Color.GRAY,
    val progressPercentage: Float = 0f,
    val currentTask: String? = null,
    val completedTasks: Int = 0,
    val remainingTasks: Int = 0,
    val isLoading: Boolean = false
)