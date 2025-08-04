package com.youtubesmm.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Simplified data for now
            val state = SettingsState(
                status = "Sozlamalar yuklandi",
                statusColor = android.graphics.Color.GREEN,
                ipRotation = true,
                delayBetweenAccounts = 30,
                taskDelay = 60,
                userAgentRotation = true,
                isLoading = false
            )
            _settingsState.value = state
        }
    }

    fun saveSettings(
        ipRotation: Boolean,
        delayBetweenAccounts: Int,
        taskDelay: Int,
        userAgentRotation: Boolean
    ) {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(
                isLoading = true,
                status = "Sozlamalar saqlanmoqda...",
                statusColor = android.graphics.Color.BLUE
            )

            // Simulate save
            kotlinx.coroutines.delay(1000)

            _settingsState.value = _settingsState.value.copy(
                isLoading = false,
                status = "Sozlamalar saqlandi",
                statusColor = android.graphics.Color.GREEN,
                ipRotation = ipRotation,
                delayBetweenAccounts = delayBetweenAccounts,
                taskDelay = taskDelay,
                userAgentRotation = userAgentRotation,
                message = "Sozlamalar muvaffaqiyatli saqlandi"
            )
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(
                isLoading = true,
                status = "Kesh tozalanmoqda...",
                statusColor = android.graphics.Color.parseColor("#FF9800")
            )

            // Simulate clear cache
            kotlinx.coroutines.delay(2000)

            _settingsState.value = _settingsState.value.copy(
                isLoading = false,
                status = "Kesh tozalandi",
                statusColor = android.graphics.Color.GREEN,
                message = "Kesh muvaffaqiyatli tozalandi"
            )
        }
    }

    fun showAbout() {
        viewModelScope.launch {
            _settingsState.value = _settingsState.value.copy(
                message = "YouTube SMM v1.0\nTuzuvchi: AI Assistant\nMaqsad: YouTube kanallarini organik o'shirish"
            )
        }
    }

    fun clearMessage() {
        _settingsState.value = _settingsState.value.copy(message = "")
    }
}

data class SettingsState(
    val status: String = "Sozlamalar",
    val statusColor: Int = android.graphics.Color.BLUE,
    val ipRotation: Boolean = true,
    val delayBetweenAccounts: Int = 30,
    val taskDelay: Int = 60,
    val userAgentRotation: Boolean = true,
    val isLoading: Boolean = false,
    val message: String = ""
)