package com.example.data.model

enum class FormatType {
    VIDEO,
    AUDIO
}

data class VideoFormat(
    val id: String,
    val qualityLabel: String,
    val resolutionLabel: String,
    val height: Int,
    val fps: Int = 30,
    val formatType: FormatType = FormatType.VIDEO,
    val codec: String = "H.264 / AAC",
    val approxFileSizeMb: Double,
    val downloadUrl: String,
    val isNoWatermark: Boolean = false,
    val isRecommended: Boolean = false
) {
    val displaySize: String
        get() = if (approxFileSizeMb >= 1000) {
            String.format("%.1f GB", approxFileSizeMb / 1024.0)
        } else {
            String.format("%.1f MB", approxFileSizeMb)
        }
}
