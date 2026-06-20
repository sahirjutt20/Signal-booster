package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "signal_logs")
data class SignalLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val dbm: Int,
    val networkType: String,
    val operatorName: String,
    val optimizationType: String, // e.g., "DNS Ping", "MTU Tuning", "Network Refresh"
    val resultMessage: String,
    val latencyMs: Long
)

@Dao
interface SignalLogDao {
    @Query("SELECT * FROM signal_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAllLogs(): Flow<List<SignalLog>>

    @Insert
    suspend fun insertLog(log: SignalLog)

    @Query("DELETE FROM signal_logs")
    suspend fun clearLogs()
}

@Database(entities = [SignalLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun signalLogDao(): SignalLogDao
}
