package com.youtubesmm.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val quantity: Int,
    val serviceType: ServiceType,
    val status: OrderStatus = OrderStatus.PENDING,
    val completedCount: Int = 0,
    val failedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val estimatedTime: Long? = null,
    val priority: Int = 1,
    val customComment: String? = null
)

enum class ServiceType(val displayName: String, val description: String) {
    VIEWS("Ko'rishlar", "Video ko'rishlarini oshirish"),
    LIKES("Layklar", "Video layklarini oshirish"),
    COMMENTS("Kommentlar", "Video kommentlarini oshirish"),
    SUBSCRIBERS("Obunachilar", "Kanal obunachilarini oshirish"),
    SHORTS_VIEWS("Shorts Ko'rishlar", "Shorts ko'rishlarini oshirish"),
    SHORTS_LIKES("Shorts Layklar", "Shorts layklarini oshirish"),
    LIVE_PARTICIPATION("Live Stream Ishtirok", "Live streamda ishtirok etish")
}

enum class OrderStatus(val displayName: String) {
    PENDING("Kutilmoqda"),
    IN_PROGRESS("Jarayonda"),
    COMPLETED("Tugallandi"),
    FAILED("Xatolik"),
    PAUSED("Pauza"),
    CANCELLED("Bekor qilindi")
}