package com.example.data.repository

import android.content.Context
import com.example.data.downloader.DownloadEngine
import com.example.data.downloader.MediaExtractor
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadDao
import com.example.data.model.DownloadItem
import com.example.data.model.VideoFormat
import com.example.data.model.VideoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import java.io.File

class DownloadRepository(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    val downloadDao: DownloadDao = database.downloadDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    val downloadEngine = DownloadEngine(context, downloadDao, scope)

    val allDownloads: Flow<List<DownloadItem>> = downloadDao.getAllDownloads()
    val favoriteDownloads: Flow<List<DownloadItem>> = downloadDao.getFavorites()

    suspend fun analyzeUrl(url: String): Result<VideoInfo> {
        return MediaExtractor.extract(url)
    }

    fun startDownload(videoInfo: VideoInfo, format: VideoFormat): String {
        return downloadEngine.startDownload(videoInfo, format)
    }

    fun pauseDownload(id: String) {
        downloadEngine.pauseDownload(id)
    }

    fun resumeDownload(item: DownloadItem) {
        downloadEngine.resumeDownload(item)
    }

    fun cancelDownload(id: String) {
        downloadEngine.cancelDownload(id)
    }

    suspend fun deleteDownload(item: DownloadItem) {
        downloadEngine.cancelDownload(item.id)
        if (item.localFilePath != null) {
            val file = File(item.localFilePath)
            if (file.exists()) {
                file.delete()
            }
        }
        downloadDao.deleteById(item.id)
    }

    suspend fun toggleFavorite(id: String, currentStatus: Boolean) {
        downloadDao.updateFavorite(id, !currentStatus)
    }

    suspend fun clearHistory() {
        downloadDao.clearAll()
    }
}
