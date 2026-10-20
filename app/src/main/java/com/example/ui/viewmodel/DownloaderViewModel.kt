package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DownloadItem
import com.example.data.model.SupportedPlatform
import com.example.data.model.VideoFormat
import com.example.data.model.VideoInfo
import com.example.data.repository.DownloadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DownloaderUiState(
    val inputUrl: String = "",
    val detectedPlatform: SupportedPlatform = SupportedPlatform.DIRECT,
    val isAnalyzing: Boolean = false,
    val extractedVideo: VideoInfo? = null,
    val analysisError: String? = null,
    val showFormatSheet: Boolean = false,
    val selectedFormat: VideoFormat? = null,
    val clipboardSuggestion: String? = null,
    val activeTab: Int = 0, // 0: Downloader, 1: Batch, 2: Library, 3: Settings
    val selectedFilterPlatform: String = "ALL", // "ALL", "YOUTUBE", "TIKTOK", "X_TWITTER", "INSTAGRAM", "REDDIT"
    val selectedFilterType: String = "ALL", // "ALL", "VIDEO", "AUDIO"
    val searchQuery: String = "",
    val previewItem: DownloadItem? = null,
    // Batch Downloader
    val batchInput: String = "",
    val batchIsAnalyzing: Boolean = false,
    val batchExtractedList: List<VideoInfo> = emptyList(),
    // Settings
    val defaultQualityPreference: String = "1080p (Full HD)",
    val downloadOverWifiOnly: Boolean = false,
    val autoDetectClipboard: Boolean = true
)

class DownloaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DownloadRepository(application)
    private val clipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    // Downloads flow from Room
    val allDownloads: StateFlow<List<DownloadItem>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered library items
    val libraryItems: StateFlow<List<DownloadItem>> = combine(
        allDownloads,
        _uiState
    ) { downloads, state ->
        downloads.filter { item ->
            val matchesPlatform = state.selectedFilterPlatform == "ALL" || item.platformName == state.selectedFilterPlatform
            val matchesType = state.selectedFilterType == "ALL" || item.formatType == state.selectedFilterType
            val matchesQuery = state.searchQuery.isBlank() || item.title.contains(state.searchQuery, ignoreCase = true)
            matchesPlatform && matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active downloading queue
    val activeDownloads: StateFlow<List<DownloadItem>> = allDownloads.combine(_uiState) { list, _ ->
        list.filter { it.isDownloading || it.isPaused || it.downloadStatus == "QUEUED" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total storage occupied
    val totalStorageFormatted: StateFlow<String> = allDownloads.combine(_uiState) { list, _ ->
        val totalBytes = list.filter { it.isCompleted }.sumOf { it.fileSizeBytes }
        formatBytes(totalBytes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0 MB")

    init {
        checkClipboardForMediaLink()
    }

    fun onUrlChanged(newUrl: String) {
        val detected = SupportedPlatform.detectFromUrl(newUrl)
        _uiState.value = _uiState.value.copy(
            inputUrl = newUrl,
            detectedPlatform = detected,
            analysisError = null
        )
    }

    fun pasteFromClipboard() {
        val clip = clipboardManager.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: ""
            if (text.isNotBlank()) {
                onUrlChanged(text.trim())
                analyzeCurrentUrl()
            }
        }
    }

    fun checkClipboardForMediaLink() {
        if (!_uiState.value.autoDetectClipboard) return
        val clip = clipboardManager.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()?.trim() ?: ""
            if (text.startsWith("http://") || text.startsWith("https://")) {
                val detected = SupportedPlatform.detectFromUrl(text)
                if (detected != SupportedPlatform.DIRECT || text.endsWith(".mp4") || text.endsWith(".mp3")) {
                    _uiState.value = _uiState.value.copy(clipboardSuggestion = text)
                }
            }
        }
    }

    fun dismissClipboardSuggestion() {
        _uiState.value = _uiState.value.copy(clipboardSuggestion = null)
    }

    fun acceptClipboardSuggestion() {
        val suggestion = _uiState.value.clipboardSuggestion ?: return
        onUrlChanged(suggestion)
        dismissClipboardSuggestion()
        analyzeCurrentUrl()
    }

    fun setSampleUrl(platform: SupportedPlatform) {
        onUrlChanged(platform.sampleUrl)
        analyzeCurrentUrl()
    }

    fun analyzeCurrentUrl() {
        val url = _uiState.value.inputUrl.trim()
        if (url.isBlank()) {
            _uiState.value = _uiState.value.copy(analysisError = "Please enter or paste a valid link first.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, analysisError = null, extractedVideo = null)
            val result = repository.analyzeUrl(url)
            result.onSuccess { info ->
                val recommended = info.formats.firstOrNull { it.isRecommended } ?: info.formats.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    extractedVideo = info,
                    selectedFormat = recommended,
                    showFormatSheet = false
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    analysisError = err.message ?: "Failed to analyze URL. Please check the link and try again."
                )
            }
        }
    }

    fun openFormatSheet() {
        _uiState.value = _uiState.value.copy(showFormatSheet = true)
    }

    fun closeFormatSheet() {
        _uiState.value = _uiState.value.copy(showFormatSheet = false)
    }

    fun selectFormat(format: VideoFormat) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
    }

    fun downloadSelectedFormat() {
        val info = _uiState.value.extractedVideo ?: return
        val format = _uiState.value.selectedFormat ?: info.formats.firstOrNull() ?: return
        repository.startDownload(info, format)
        _uiState.value = _uiState.value.copy(
            showFormatSheet = false,
            // Switch to library tab or keep on downloader
            activeTab = 0
        )
    }

    fun startInstantDownload(info: VideoInfo, format: VideoFormat) {
        repository.startDownload(info, format)
    }

    fun pauseDownload(id: String) {
        repository.pauseDownload(id)
    }

    fun resumeDownload(item: DownloadItem) {
        repository.resumeDownload(item)
    }

    fun cancelDownload(id: String) {
        repository.cancelDownload(id)
    }

    fun deleteItem(item: DownloadItem) {
        viewModelScope.launch {
            repository.deleteDownload(item)
        }
    }

    fun toggleFavorite(item: DownloadItem) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, item.isFavorite)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    fun setPlatformFilter(platform: String) {
        _uiState.value = _uiState.value.copy(selectedFilterPlatform = platform)
    }

    fun setTypeFilter(type: String) {
        _uiState.value = _uiState.value.copy(selectedFilterType = type)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun openPreview(item: DownloadItem) {
        _uiState.value = _uiState.value.copy(previewItem = item)
    }

    fun closePreview() {
        _uiState.value = _uiState.value.copy(previewItem = null)
    }

    // Batch Downloader
    fun onBatchInputChanged(text: String) {
        _uiState.value = _uiState.value.copy(batchInput = text)
    }

    fun analyzeBatchUrls() {
        val lines = _uiState.value.batchInput.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(batchIsAnalyzing = true, batchExtractedList = emptyList())
            val extracted = mutableListOf<VideoInfo>()
            for (url in lines.take(10)) {
                val res = repository.analyzeUrl(url)
                res.onSuccess { info ->
                    extracted.add(info)
                }
            }
            _uiState.value = _uiState.value.copy(
                batchIsAnalyzing = false,
                batchExtractedList = extracted
            )
        }
    }

    fun downloadAllBatch() {
        val list = _uiState.value.batchExtractedList
        for (info in list) {
            val format = info.formats.firstOrNull { it.isRecommended } ?: info.formats.firstOrNull() ?: continue
            repository.startDownload(info, format)
        }
        _uiState.value = _uiState.value.copy(
            batchInput = "",
            batchExtractedList = emptyList(),
            activeTab = 0
        )
    }

    // Settings
    fun updateWifiOnly(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(downloadOverWifiOnly = enabled)
    }

    fun updateAutoClipboard(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoDetectClipboard = enabled)
    }

    fun updateDefaultQuality(quality: String) {
        _uiState.value = _uiState.value.copy(defaultQualityPreference = quality)
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.1f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
