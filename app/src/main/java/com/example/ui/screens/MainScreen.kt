package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FormatSelectorSheet
import com.example.ui.components.VideoPlayerDialog
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.ElectricViolet
import com.example.ui.viewmodel.DownloaderViewModel

@Composable
fun MainScreen(
    viewModel: DownloaderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val libraryItems by viewModel.libraryItems.collectAsState()
    val allDownloads by viewModel.allDownloads.collectAsState()
    val totalStorage by viewModel.totalStorageFormatted.collectAsState()

    // Periodically re-check clipboard when on home
    LaunchedEffect(uiState.activeTab) {
        if (uiState.activeTab == 0) {
            viewModel.checkClipboardForMediaLink()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Downloader (Home)
                NavigationBarItem(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = {
                        if (activeDownloads.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = CyanNeon, contentColor = Color(0xFF090D16)) {
                                        Text("${activeDownloads.size}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Downloader")
                            }
                        } else {
                            Icon(Icons.Default.Download, contentDescription = "Downloader")
                        }
                    },
                    label = { Text("Downloader", fontSize = 12.sp, fontWeight = if (uiState.activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF090D16),
                        indicatorColor = CyanNeon,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = CyanNeon,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_downloader")
                )

                // Batch
                NavigationBarItem(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = {
                        Icon(Icons.Default.Layers, contentDescription = "Batch")
                    },
                    label = { Text("Batch", fontSize = 12.sp, fontWeight = if (uiState.activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF090D16),
                        indicatorColor = CyanNeon,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = CyanNeon,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_batch")
                )

                // Library
                NavigationBarItem(
                    selected = uiState.activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = {
                        val completedCount = allDownloads.count { it.isCompleted }
                        if (completedCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = ElectricViolet, contentColor = Color.White) {
                                        Text("$completedCount", fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = "Library")
                            }
                        } else {
                            Icon(Icons.Default.VideoLibrary, contentDescription = "Library")
                        }
                    },
                    label = { Text("Library", fontSize = 12.sp, fontWeight = if (uiState.activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF090D16),
                        indicatorColor = CyanNeon,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = CyanNeon,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_library")
                )

                // Settings
                NavigationBarItem(
                    selected = uiState.activeTab == 3,
                    onClick = { viewModel.setActiveTab(3) },
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    },
                    label = { Text("Settings", fontSize = 12.sp, fontWeight = if (uiState.activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF090D16),
                        indicatorColor = CyanNeon,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = CyanNeon,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> HomeScreen(
                        uiState = uiState,
                        activeDownloads = activeDownloads,
                        onUrlChanged = viewModel::onUrlChanged,
                        onPasteClicked = viewModel::pasteFromClipboard,
                        onAnalyzeClicked = viewModel::analyzeCurrentUrl,
                        onPlatformSampleClicked = viewModel::setSampleUrl,
                        onDismissClipboard = viewModel::dismissClipboardSuggestion,
                        onAcceptClipboard = viewModel::acceptClipboardSuggestion,
                        onOpenFormatSheet = viewModel::openFormatSheet,
                        onDownloadCurrentFormat = viewModel::downloadSelectedFormat,
                        onPauseDownload = viewModel::pauseDownload,
                        onResumeDownload = viewModel::resumeDownload,
                        onCancelDownload = viewModel::cancelDownload
                    )
                    1 -> BatchScreen(
                        batchInput = uiState.batchInput,
                        isAnalyzing = uiState.batchIsAnalyzing,
                        extractedList = uiState.batchExtractedList,
                        onBatchInputChanged = viewModel::onBatchInputChanged,
                        onAnalyzeBatch = viewModel::analyzeBatchUrls,
                        onDownloadAll = viewModel::downloadAllBatch
                    )
                    2 -> LibraryScreen(
                        libraryItems = libraryItems,
                        searchQuery = uiState.searchQuery,
                        selectedPlatformFilter = uiState.selectedFilterPlatform,
                        selectedTypeFilter = uiState.selectedFilterType,
                        totalStorageFormatted = totalStorage,
                        onSearchQueryChanged = viewModel::setSearchQuery,
                        onPlatformFilterSelected = viewModel::setPlatformFilter,
                        onTypeFilterSelected = viewModel::setTypeFilter,
                        onPlayItem = viewModel::openPreview,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onDeleteItem = viewModel::deleteItem,
                        onClearAllHistory = viewModel::clearHistory,
                        onNavigateToHome = { viewModel.setActiveTab(0) }
                    )
                    3 -> SettingsScreen(
                        defaultQuality = uiState.defaultQualityPreference,
                        wifiOnly = uiState.downloadOverWifiOnly,
                        autoClipboard = uiState.autoDetectClipboard,
                        onQualitySelected = viewModel::updateDefaultQuality,
                        onWifiOnlyChanged = viewModel::updateWifiOnly,
                        onAutoClipboardChanged = viewModel::updateAutoClipboard
                    )
                }
            }
        }
    }

    // Modal Format Selector Bottom Sheet
    if (uiState.showFormatSheet && uiState.extractedVideo != null) {
        FormatSelectorSheet(
            videoInfo = uiState.extractedVideo!!,
            selectedFormat = uiState.selectedFormat,
            onFormatSelected = viewModel::selectFormat,
            onDownloadConfirmed = viewModel::downloadSelectedFormat,
            onDismiss = viewModel::closeFormatSheet
        )
    }

    // Modal Video Player Dialog
    if (uiState.previewItem != null) {
        VideoPlayerDialog(
            item = uiState.previewItem!!,
            onDismiss = viewModel::closePreview
        )
    }
}
