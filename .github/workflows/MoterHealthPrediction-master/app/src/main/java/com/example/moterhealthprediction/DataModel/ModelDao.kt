package com.example.moterhealthprediction.DataModel
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface MotorDao {
    @Query("SELECT * FROM motor")
    fun getAllMotors(): Flow<List<Motor>> // Use Flow to observe database changes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(motor: Motor)

    @Delete
    suspend fun delete(motor: Motor)
}

