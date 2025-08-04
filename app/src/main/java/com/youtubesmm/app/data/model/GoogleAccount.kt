package com.youtubesmm.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "google_accounts")
data class GoogleAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String,
    val isActive: Boolean = true,
    val lastUsed: Long = 0,
    val useCount: Int = 0,
    val isBlocked: Boolean = false,
    val blockReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val uniqueId: String = generateUniqueId()
) {
    companion object {
        private fun generateUniqueId(): String {
            return "acc_${System.currentTimeMillis()}_${(0..9999).random()}"
        }
    }
}