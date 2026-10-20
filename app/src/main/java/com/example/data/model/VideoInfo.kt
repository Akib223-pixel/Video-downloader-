package com.example.data.model

data class VideoInfo(
    val url: String,
    val platform: SupportedPlatform,
    val title: String,
    val author: String,
    val authorHandle: String,
    val durationSeconds: Int,
    val formattedDuration: String,
    val thumbnailUri: String,
    val viewCount: String,
    val likesCount: String,
    val description: String,
    val formats: List<VideoFormat>
)
