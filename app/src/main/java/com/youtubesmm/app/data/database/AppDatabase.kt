package com.youtubesmm.app.data.database

import androidx.room.*
import com.youtubesmm.app.data.model.GoogleAccount
import com.youtubesmm.app.data.model.Order
import com.youtubesmm.app.data.model.Task
import com.youtubesmm.app.data.model.ServiceType
import com.youtubesmm.app.data.model.OrderStatus
import com.youtubesmm.app.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [GoogleAccount::class, Order::class, Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun googleAccountDao(): GoogleAccountDao
    abstract fun orderDao(): OrderDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "youtube_smm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface GoogleAccountDao {
    @Query("SELECT * FROM google_accounts WHERE isActive = 1 AND isBlocked = 0 ORDER BY lastUsed ASC")
    fun getActiveAccounts(): Flow<List<GoogleAccount>>

    @Query("SELECT * FROM google_accounts WHERE isActive = 1 AND isBlocked = 0 ORDER BY lastUsed ASC LIMIT 1")
    fun getNextAvailableAccount(): GoogleAccount?

    @Insert
    fun insertAccount(account: GoogleAccount): Long

    @Insert
    fun insertAccounts(accounts: List<GoogleAccount>): List<Long>

    @Update
    fun updateAccount(account: GoogleAccount): Int

    @Query("UPDATE google_accounts SET lastUsed = :timestamp, useCount = useCount + 1 WHERE id = :accountId")
    fun markAccountAsUsed(accountId: Long, timestamp: Long = System.currentTimeMillis()): Int

    @Query("UPDATE google_accounts SET isBlocked = 1, blockReason = :reason WHERE id = :accountId")
    fun blockAccount(accountId: Long, reason: String): Int

    @Query("SELECT COUNT(*) FROM google_accounts WHERE isActive = 1 AND isBlocked = 0")
    fun getActiveAccountCount(): Int

    @Query("DELETE FROM google_accounts")
    fun clearAllAccounts(): Int
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status IN ('PENDING', 'IN_PROGRESS') ORDER BY priority DESC, createdAt ASC")
    fun getActiveOrders(): Flow<List<Order>>

    @Insert
    fun insertOrder(order: Order): Long

    @Update
    fun updateOrder(order: Order): Int

    @Query("UPDATE orders SET status = :status, startedAt = :startedAt WHERE id = :orderId")
    fun updateOrderStatus(orderId: Long, status: String, startedAt: Long? = null): Int

    @Query("UPDATE orders SET completedCount = completedCount + 1 WHERE id = :orderId")
    fun incrementCompletedCount(orderId: Long): Int

    @Query("UPDATE orders SET failedCount = failedCount + 1 WHERE id = :orderId")
    fun incrementFailedCount(orderId: Long): Int

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: Long): Order?
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE orderId = :orderId ORDER BY priority DESC, createdAt ASC")
    fun getTasksByOrder(orderId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY priority DESC, createdAt ASC LIMIT 1")
    fun getNextPendingTask(): Task?

    @Insert
    fun insertTask(task: Task): Long

    @Insert
    fun insertTasks(tasks: List<Task>): List<Long>

    @Update
    fun updateTask(task: Task): Int

    @Query("UPDATE tasks SET status = :status, startedAt = :startedAt WHERE id = :taskId")
    fun updateTaskStatus(taskId: Long, status: String, startedAt: Long? = null): Int

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt, errorMessage = :errorMessage WHERE id = :taskId")
    fun completeTask(taskId: Long, status: String, completedAt: Long = System.currentTimeMillis(), errorMessage: String? = null): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE orderId = :orderId")
    fun getTaskCountByOrder(orderId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE orderId = :orderId AND status = 'COMPLETED'")
    fun getCompletedTaskCountByOrder(orderId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE orderId = :orderId AND status = 'FAILED'")
    fun getFailedTaskCountByOrder(orderId: Long): Int
}

class Converters {
    @TypeConverter
    fun fromServiceType(value: ServiceType): String = value.name

    @TypeConverter
    fun toServiceType(value: String): ServiceType = ServiceType.valueOf(value)

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = OrderStatus.valueOf(value)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)
}