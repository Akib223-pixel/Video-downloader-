package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DownloadItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_items ORDER BY createdAt DESC")
    fun getAllDownloads(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_items WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_items WHERE platformName = :platform ORDER BY createdAt DESC")
    fun getByPlatform(platform: String): Flow<List<DownloadItem>>

    @Query("SELECT * FROM download_items WHERE id = :id")
    suspend fun getById(id: String): DownloadItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DownloadItem)

    @Update
    suspend fun update(item: DownloadItem)

    @Delete
    suspend fun delete(item: DownloadItem)

    @Query("DELETE FROM download_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE download_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE download_items SET progressPercent = :progress, downloadSpeed = :speed, etaSeconds = :eta, downloadStatus = :status WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, speed: String, eta: Int, status: String)

    @Query("UPDATE download_items SET downloadStatus = 'COMPLETED', progressPercent = 100, localFilePath = :localPath, fileSizeBytes = :sizeBytes WHERE id = :id")
    suspend fun markCompleted(id: String, localPath: String, sizeBytes: Long)

    @Query("UPDATE download_items SET downloadStatus = 'FAILED', errorMessage = :error WHERE id = :id")
    suspend fun markFailed(id: String, error: String)

    @Query("DELETE FROM download_items")
    suspend fun clearAll()
}
