package com.youtubesmm.app.data.database

import androidx.room.*
import com.youtubesmm.app.data.model.GoogleAccount
import com.youtubesmm.app.data.model.Order
import com.youtubesmm.app.data.model.Task
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
        
        fun getInstance(): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    android.content.Context.getApplicationContext(),
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
    suspend fun getNextAvailableAccount(): GoogleAccount?
    
    @Insert
    suspend fun insertAccount(account: GoogleAccount): Long
    
    @Insert
    suspend fun insertAccounts(accounts: List<GoogleAccount>)
    
    @Update
    suspend fun updateAccount(account: GoogleAccount)
    
    @Query("UPDATE google_accounts SET lastUsed = :timestamp, useCount = useCount + 1 WHERE id = :accountId")
    suspend fun markAccountAsUsed(accountId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE google_accounts SET isBlocked = 1, blockReason = :reason WHERE id = :accountId")
    suspend fun blockAccount(accountId: Long, reason: String)
    
    @Query("SELECT COUNT(*) FROM google_accounts WHERE isActive = 1 AND isBlocked = 0")
    suspend fun getActiveAccountCount(): Int
    
    @Query("DELETE FROM google_accounts")
    suspend fun clearAllAccounts()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE status IN ('PENDING', 'IN_PROGRESS') ORDER BY priority DESC, createdAt ASC")
    fun getActiveOrders(): Flow<List<Order>>
    
    @Insert
    suspend fun insertOrder(order: Order): Long
    
    @Update
    suspend fun updateOrder(order: Order)
    
    @Query("UPDATE orders SET status = :status, startedAt = :startedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String, startedAt: Long? = null)
    
    @Query("UPDATE orders SET completedCount = completedCount + 1 WHERE id = :orderId")
    suspend fun incrementCompletedCount(orderId: Long)
    
    @Query("UPDATE orders SET failedCount = failedCount + 1 WHERE id = :orderId")
    suspend fun incrementFailedCount(orderId: Long)
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): Order?
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE orderId = :orderId ORDER BY priority DESC, createdAt ASC")
    fun getTasksByOrder(orderId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY priority DESC, createdAt ASC LIMIT 1")
    suspend fun getNextPendingTask(): Task?
    
    @Insert
    suspend fun insertTask(task: Task): Long
    
    @Insert
    suspend fun insertTasks(tasks: List<Task>)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Query("UPDATE tasks SET status = :status, startedAt = :startedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, status: String, startedAt: Long? = null)
    
    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt, errorMessage = :errorMessage WHERE id = :taskId")
    suspend fun completeTask(taskId: Long, status: String, completedAt: Long = System.currentTimeMillis(), errorMessage: String? = null)
    
    @Query("SELECT COUNT(*) FROM tasks WHERE orderId = :orderId")
    suspend fun getTaskCountByOrder(orderId: Long): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE orderId = :orderId AND status = 'COMPLETED'")
    suspend fun getCompletedTaskCountByOrder(orderId: Long): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE orderId = :orderId AND status = 'FAILED'")
    suspend fun getFailedTaskCountByOrder(orderId: Long): Int
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