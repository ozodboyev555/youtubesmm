package com.youtubesmm.app.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {

    private val _accountsState = MutableStateFlow(AccountsState())
    val accountsState: StateFlow<AccountsState> = _accountsState

    init {
        loadAccountsData()
    }

    private fun loadAccountsData() {
        viewModelScope.launch {
            // Simplified data for now
            val state = AccountsState(
                status = "Hisoblar yuklandi",
                statusColor = android.graphics.Color.GREEN,
                totalAccounts = 0,
                activeAccounts = 0,
                blockedAccounts = 0,
                isLoading = false
            )
            _accountsState.value = state
        }
    }

    fun importAccounts() {
        viewModelScope.launch {
            _accountsState.value = _accountsState.value.copy(
                isLoading = true,
                status = "Hisoblar yuklanmoqda...",
                statusColor = android.graphics.Color.BLUE
            )

            // Simulate import
            kotlinx.coroutines.delay(2000)

            _accountsState.value = _accountsState.value.copy(
                isLoading = false,
                status = "Hisoblar muvaffaqiyatli yuklandi",
                statusColor = android.graphics.Color.GREEN,
                totalAccounts = 100,
                activeAccounts = 95,
                blockedAccounts = 5,
                message = "100 ta hisob yuklandi"
            )
        }
    }

    fun exportAccounts() {
        viewModelScope.launch {
            _accountsState.value = _accountsState.value.copy(
                isLoading = true,
                status = "Hisoblar eksport qilinmoqda...",
                statusColor = android.graphics.Color.BLUE
            )

            // Simulate export
            kotlinx.coroutines.delay(1000)

            _accountsState.value = _accountsState.value.copy(
                isLoading = false,
                status = "Hisoblar eksport qilindi",
                statusColor = android.graphics.Color.GREEN,
                message = "Hisoblar faylga saqlandi"
            )
        }
    }

    fun clearAccounts() {
        viewModelScope.launch {
            _accountsState.value = _accountsState.value.copy(
                isLoading = true,
                status = "Hisoblar tozalanmoqda...",
                statusColor = android.graphics.Color.parseColor("#FF9800")
            )

            // Simulate clear
            kotlinx.coroutines.delay(500)

            _accountsState.value = _accountsState.value.copy(
                isLoading = false,
                status = "Hisoblar tozalandi",
                statusColor = android.graphics.Color.GREEN,
                totalAccounts = 0,
                activeAccounts = 0,
                blockedAccounts = 0,
                message = "Barcha hisoblar o'chirildi"
            )
        }
    }

    fun clearMessage() {
        _accountsState.value = _accountsState.value.copy(message = "")
    }
}

data class AccountsState(
    val status: String = "Hisoblar",
    val statusColor: Int = android.graphics.Color.BLUE,
    val totalAccounts: Int = 0,
    val activeAccounts: Int = 0,
    val blockedAccounts: Int = 0,
    val isLoading: Boolean = false,
    val message: String = ""
)