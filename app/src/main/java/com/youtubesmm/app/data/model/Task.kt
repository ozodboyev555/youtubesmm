package com.youtubesmm.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val accountId: Long,
    val serviceType: ServiceType,
    val status: TaskStatus = TaskStatus.PENDING,
    val url: String,
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val errorMessage: String? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val priority: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskStatus(val displayName: String) {
    PENDING("Kutilmoqda"),
    IN_PROGRESS("Jarayonda"),
    COMPLETED("Tugallandi"),
    FAILED("Xatolik"),
    RETRY("Qayta urinish"),
    CANCELLED("Bekor qilindi")
}

data class TaskProgress(
    val totalTasks: Int,
    val completedTasks: Int,
    val failedTasks: Int,
    val pendingTasks: Int,
    val inProgressTasks: Int,
    val progressPercentage: Float,
    val estimatedTimeRemaining: Long? = null
) {
    val remainingTasks: Int
        get() = totalTasks - completedTasks - failedTasks
}