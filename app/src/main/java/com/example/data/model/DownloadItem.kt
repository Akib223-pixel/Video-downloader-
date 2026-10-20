package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "download_items")
data class DownloadItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val originalUrl: String,
    val platformName: String, // from SupportedPlatform.name
    val qualityLabel: String,
    val formatType: String, // "VIDEO" or "AUDIO"
    val localFilePath: String? = null,
    val thumbnailUri: String,
    val durationFormatted: String,
    val fileSizeBytes: Long = 0L,
    val approxFileSizeFormatted: String,
    val downloadStatus: String, // "QUEUED", "DOWNLOADING", "PAUSED", "COMPLETED", "FAILED"
    val progressPercent: Int = 0,
    val downloadSpeed: String = "",
    val etaSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
) {
    val isCompleted: Boolean
        get() = downloadStatus == "COMPLETED"

    val isDownloading: Boolean
        get() = downloadStatus == "DOWNLOADING"

    val isPaused: Boolean
        get() = downloadStatus == "PAUSED"

    val isFailed: Boolean
        get() = downloadStatus == "FAILED"
}
