package com.youtubesmm.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youtubesmm.app.data.model.ServiceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val _orderState = MutableStateFlow(OrderState())
    val orderState: StateFlow<OrderState> = _orderState

    fun submitOrder(url: String, quantity: Int, serviceType: ServiceType, comment: String?) {
        viewModelScope.launch {
            _orderState.value = _orderState.value.copy(isLoading = true)
            
            // Simulate order submission
            kotlinx.coroutines.delay(2000)
            
            _orderState.value = _orderState.value.copy(
                isLoading = false,
                isSuccess = true,
                message = "Buyurtma muvaffaqiyatli yuborildi!"
            )
        }
    }

    fun resetState() {
        _orderState.value = OrderState()
    }
}

data class OrderState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val message: String = ""
)