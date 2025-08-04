package com.youtubesmm.app.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youtubesmm.app.data.database.AppDatabase
import com.youtubesmm.app.data.model.Order
import com.youtubesmm.app.data.model.ServiceType
import com.youtubesmm.app.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    
    private val database = AppDatabase.getInstance()
    
    private val _orderState = MutableStateFlow(OrderState())
    val orderState: StateFlow<OrderState> = _orderState.asStateFlow()
    
    fun createOrder(url: String, quantity: Int, serviceType: ServiceType, customComment: String?) {
        viewModelScope.launch {
            try {
                _orderState.value = _orderState.value.copy(isLoading = true, error = null)
                
                // Buyurtmani yaratish
                val order = Order(
                    url = url,
                    quantity = quantity,
                    serviceType = serviceType,
                    customComment = customComment.takeIf { !it.isNullOrEmpty() }
                )
                
                val orderId = database.orderDao().insertOrder(order)
                
                // Vazifalarni yaratish
                val tasks = mutableListOf<Task>()
                for (i in 1..quantity) {
                    val task = Task(
                        orderId = orderId,
                        accountId = 0, // Keyin ajratiladi
                        serviceType = serviceType,
                        url = url,
                        customComment = customComment
                    )
                    tasks.add(task)
                }
                
                database.taskDao().insertTasks(tasks)
                
                _orderState.value = _orderState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
                
            } catch (e: Exception) {
                _orderState.value = _orderState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Noma'lum xatolik"
                )
            }
        }
    }
    
    fun resetState() {
        _orderState.value = OrderState()
    }
}

data class OrderState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)