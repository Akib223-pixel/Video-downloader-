package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ColorInstagram
import com.example.ui.theme.ColorReddit
import com.example.ui.theme.ColorTikTokPink
import com.example.ui.theme.ColorTwitterX
import com.example.ui.theme.ColorYouTube
import com.example.ui.theme.CyanNeon

enum class SupportedPlatform(
    val id: String,
    val displayName: String,
    val hostKeywords: List<String>,
    val brandColor: Color,
    val tagLabel: String,
    val placeholderHint: String,
    val sampleUrl: String
) {
    YOUTUBE(
        id = "youtube",
        displayName = "YouTube",
        hostKeywords = listOf("youtube.com", "youtu.be"),
        brandColor = ColorYouTube,
        tagLabel = "YouTube / Shorts",
        placeholderHint = "https://youtube.com/watch?v=... or shorts/...",
        sampleUrl = "https://youtube.com/shorts/sample_cosmos_clip"
    ),
    TIKTOK(
        id = "tiktok",
        displayName = "TikTok",
        hostKeywords = listOf("tiktok.com"),
        brandColor = ColorTikTokPink,
        tagLabel = "TikTok (No Watermark)",
        placeholderHint = "https://www.tiktok.com/@user/video/...",
        sampleUrl = "https://www.tiktok.com/@nature_explorer/video/728192384"
    ),
    X_TWITTER(
        id = "x_twitter",
        displayName = "X / Twitter",
        hostKeywords = listOf("twitter.com", "x.com"),
        brandColor = Color(0xFF1DA1F2),
        tagLabel = "X (Twitter)",
        placeholderHint = "https://x.com/user/status/... or twitter.com/...",
        sampleUrl = "https://x.com/TechHub/status/1789234821034"
    ),
    INSTAGRAM(
        id = "instagram",
        displayName = "Instagram",
        hostKeywords = listOf("instagram.com", "instagr.am"),
        brandColor = ColorInstagram,
        tagLabel = "Instagram Reels / Post",
        placeholderHint = "https://www.instagram.com/reel/... or /p/...",
        sampleUrl = "https://www.instagram.com/reel/C89xK1pvZa"
    ),
    REDDIT(
        id = "reddit",
        displayName = "Reddit",
        hostKeywords = listOf("reddit.com", "redd.it"),
        brandColor = ColorReddit,
        tagLabel = "Reddit Video",
        placeholderHint = "https://reddit.com/r/.../comments/... or v.redd.it/...",
        sampleUrl = "https://reddit.com/r/EarthPorn/comments/v83921/aurora_timelapse"
    ),
    DIRECT(
        id = "direct",
        displayName = "Direct Link",
        hostKeywords = emptyList(),
        brandColor = CyanNeon,
        tagLabel = "Direct Video Stream",
        placeholderHint = "https://example.com/video.mp4",
        sampleUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    );

    companion object {
        fun detectFromUrl(url: String): SupportedPlatform {
            val cleanUrl = url.trim().lowercase()
            return entries.firstOrNull { platform ->
                platform.hostKeywords.any { kw -> cleanUrl.contains(kw) }
            } ?: DIRECT
        }
    }
}
