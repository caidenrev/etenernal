package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Couple Profile Queries
    @Query("SELECT * FROM couple_profile WHERE id = 1 LIMIT 1")
    fun getCoupleProfileFlow(): Flow<CoupleProfile?>

    @Query("SELECT * FROM couple_profile WHERE id = 1 LIMIT 1")
    suspend fun getCoupleProfile(): CoupleProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupleProfile(profile: CoupleProfile)

    @Update
    suspend fun updateCoupleProfile(profile: CoupleProfile)

    // Memory Queries
    @Query("SELECT * FROM memories ORDER BY dateLong DESC")
    fun getAllMemoriesFlow(): Flow<List<Memory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: Memory)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: String)

    @Query("SELECT * FROM memories WHERE title LIKE '%' || :query || '%' OR storyBlog LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY dateLong DESC")
    fun searchMemoriesFlow(query: String): Flow<List<Memory>>

    // Schedule Queries
    @Query("SELECT * FROM schedules ORDER BY eventDateLong ASC")
    fun getAllSchedulesFlow(): Flow<List<Schedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteSchedule(id: String)

    // Wishlist Queries
    @Query("SELECT * FROM wishlist ORDER BY isCompleted ASC, itemName ASC")
    fun getAllWishlistFlow(): Flow<List<WishlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItem)

    @Query("UPDATE wishlist SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun toggleWishlistItem(id: String, isCompleted: Boolean)

    @Query("DELETE FROM wishlist WHERE id = :id")
    suspend fun deleteWishlistItem(id: String)

    // Alerts Queries
    @Query("SELECT * FROM alerts ORDER BY timestamp DESC")
    fun getAllAlertsFlow(): Flow<List<AlertNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertNotification)

    @Query("UPDATE alerts SET isRead = 1")
    suspend fun markAllAlertsRead()

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)

    @Query("DELETE FROM alerts")
    suspend fun clearAllAlerts()
}
