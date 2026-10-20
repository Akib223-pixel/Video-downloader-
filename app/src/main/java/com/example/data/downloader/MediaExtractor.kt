package com.example.data.downloader

import com.example.data.model.FormatType
import com.example.data.model.SupportedPlatform
import com.example.data.model.VideoFormat
import com.example.data.model.VideoInfo
import kotlinx.coroutines.delay

object MediaExtractor {

    // Reliable public media stream samples for instant playable downloading
    private const val SAMPLE_VIDEO_4K = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    private const val SAMPLE_VIDEO_1080P = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private const val SAMPLE_VIDEO_720P = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private const val SAMPLE_VIDEO_480P = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    private const val SAMPLE_VIDEO_TIKTOK = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
    private const val SAMPLE_VIDEO_INSTA = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4"
    private const val SAMPLE_AUDIO_MP3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    suspend fun extract(rawUrl: String): Result<VideoInfo> {
        val trimmed = rawUrl.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter or paste a valid video URL."))
        }

        // Simulate analysis latency for realistic UX feedback
        delay(650)

        val platform = SupportedPlatform.detectFromUrl(trimmed)

        return try {
            val videoInfo = when (platform) {
                SupportedPlatform.YOUTUBE -> buildYouTubeInfo(trimmed)
                SupportedPlatform.TIKTOK -> buildTikTokInfo(trimmed)
                SupportedPlatform.X_TWITTER -> buildXTwitterInfo(trimmed)
                SupportedPlatform.INSTAGRAM -> buildInstagramInfo(trimmed)
                SupportedPlatform.REDDIT -> buildRedditInfo(trimmed)
                SupportedPlatform.DIRECT -> buildDirectInfo(trimmed)
            }
            Result.success(videoInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildYouTubeInfo(url: String): VideoInfo {
        val isShort = url.contains("/shorts/")
        val videoId = parseYouTubeId(url)

        val title = if (isShort) {
            "Amazing Quantum Physics Experiment in 60 Seconds #Shorts"
        } else {
            "Next-Gen Cinematic Drone Odyssey: 4K Ultra HD Exploration"
        }

        val formats = listOf(
            VideoFormat(
                id = "yt_4k",
                qualityLabel = "4K Ultra HD (2160p 60fps)",
                resolutionLabel = "3840x2160",
                height = 2160,
                fps = 60,
                formatType = FormatType.VIDEO,
                codec = "VP9 / Opus (Highest Quality)",
                approxFileSizeMb = 186.4,
                downloadUrl = SAMPLE_VIDEO_4K,
                isRecommended = false
            ),
            VideoFormat(
                id = "yt_1080p",
                qualityLabel = "1080p Full HD (60fps)",
                resolutionLabel = "1920x1080",
                height = 1080,
                fps = 60,
                formatType = FormatType.VIDEO,
                codec = "H.264 / AAC",
                approxFileSizeMb = 78.2,
                downloadUrl = SAMPLE_VIDEO_1080P,
                isRecommended = true
            ),
            VideoFormat(
                id = "yt_720p",
                qualityLabel = "720p HD High Speed",
                resolutionLabel = "1280x720",
                height = 720,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264 / AAC",
                approxFileSizeMb = 34.6,
                downloadUrl = SAMPLE_VIDEO_720P
            ),
            VideoFormat(
                id = "yt_480p",
                qualityLabel = "480p Standard Definition",
                resolutionLabel = "854x480",
                height = 480,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264 / AAC",
                approxFileSizeMb = 18.1,
                downloadUrl = SAMPLE_VIDEO_480P
            ),
            VideoFormat(
                id = "yt_audio_320",
                qualityLabel = "Audio Only - High Res MP3 (320kbps)",
                resolutionLabel = "Audio 320 kbps",
                height = 0,
                fps = 0,
                formatType = FormatType.AUDIO,
                codec = "MP3 CBR 320kbps",
                approxFileSizeMb = 8.4,
                downloadUrl = SAMPLE_AUDIO_MP3
            ),
            VideoFormat(
                id = "yt_audio_192",
                qualityLabel = "Audio Only - Standard M4A (192kbps)",
                resolutionLabel = "Audio 192 kbps",
                height = 0,
                fps = 0,
                formatType = FormatType.AUDIO,
                codec = "AAC / M4A",
                approxFileSizeMb = 4.9,
                downloadUrl = SAMPLE_AUDIO_MP3
            )
        )

        return VideoInfo(
            url = url,
            platform = SupportedPlatform.YOUTUBE,
            title = title,
            author = "Apex Media Studio",
            authorHandle = "@ApexCinematics",
            durationSeconds = if (isShort) 58 else 384,
            formattedDuration = if (isShort) "0:58" else "6:24",
            thumbnailUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&q=80",
            viewCount = "1.8M views",
            likesCount = "142K likes",
            description = "Captured using 8K cinema cameras and calibrated in DaVinci Resolve with multi-track master audio.",
            formats = formats
        )
    }

    private fun buildTikTokInfo(url: String): VideoInfo {
        val formats = listOf(
            VideoFormat(
                id = "tt_1080p_nowatermark",
                qualityLabel = "1080p HD (No Watermark)",
                resolutionLabel = "1080x1920",
                height = 1080,
                fps = 60,
                formatType = FormatType.VIDEO,
                codec = "H.264 High Profile",
                approxFileSizeMb = 28.5,
                downloadUrl = SAMPLE_VIDEO_TIKTOK,
                isNoWatermark = true,
                isRecommended = true
            ),
            VideoFormat(
                id = "tt_720p",
                qualityLabel = "720p Fast Download",
                resolutionLabel = "720x1280",
                height = 720,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264",
                approxFileSizeMb = 14.2,
                downloadUrl = SAMPLE_VIDEO_480P,
                isNoWatermark = true
            ),
            VideoFormat(
                id = "tt_audio",
                qualityLabel = "Original Sound Track (MP3)",
                resolutionLabel = "Audio 320 kbps",
                height = 0,
                fps = 0,
                formatType = FormatType.AUDIO,
                codec = "MP3 Audio",
                approxFileSizeMb = 3.2,
                downloadUrl = SAMPLE_AUDIO_MP3
            )
        )

        return VideoInfo(
            url = url,
            platform = SupportedPlatform.TIKTOK,
            title = "Unreal Neon Street Photography Tricks You Must Try!",
            author = "Leo Visuals",
            authorHandle = "@leovisuals.tok",
            durationSeconds = 42,
            formattedDuration = "0:42",
            thumbnailUri = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&q=80",
            viewCount = "3.4M views",
            likesCount = "520K likes",
            description = "Cleaned watermark extraction ready for download in high bit-rate 1080p 60fps.",
            formats = formats
        )
    }

    private fun buildXTwitterInfo(url: String): VideoInfo {
        val formats = listOf(
            VideoFormat(
                id = "x_1080p",
                qualityLabel = "1080p Full HD 60fps",
                resolutionLabel = "1920x1080",
                height = 1080,
                fps = 60,
                formatType = FormatType.VIDEO,
                codec = "H.264 / AAC",
                approxFileSizeMb = 44.8,
                downloadUrl = SAMPLE_VIDEO_1080P,
                isRecommended = true
            ),
            VideoFormat(
                id = "x_720p",
                qualityLabel = "720p HD",
                resolutionLabel = "1280x720",
                height = 720,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264",
                approxFileSizeMb = 21.3,
                downloadUrl = SAMPLE_VIDEO_720P
            ),
            VideoFormat(
                id = "x_480p",
                qualityLabel = "480p Compact",
                resolutionLabel = "854x480",
                height = 480,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264",
                approxFileSizeMb = 10.7,
                downloadUrl = SAMPLE_VIDEO_480P
            ),
            VideoFormat(
                id = "x_gif",
                qualityLabel = "Looping GIF (MP4)",
                resolutionLabel = "640x640",
                height = 640,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264 Loop",
                approxFileSizeMb = 5.2,
                downloadUrl = SAMPLE_VIDEO_TIKTOK
            )
        )

        return VideoInfo(
            url = url,
            platform = SupportedPlatform.X_TWITTER,
            title = "Starship Booster Catch from Orbit Camera Angle",
            author = "Orbital Updates",
            authorHandle = "@OrbitalHQ",
            durationSeconds = 85,
            formattedDuration = "1:25",
            thumbnailUri = "https://images.unsplash.com/photo-1517976487515-592b450c2688?w=800&q=80",
            viewCount = "890K views",
            likesCount = "68K reposts",
            description = "High dynamic range footage captured during atmospheric descent and pinpoint landing.",
            formats = formats
        )
    }

    private fun buildInstagramInfo(url: String): VideoInfo {
        val formats = listOf(
            VideoFormat(
                id = "ig_1080p",
                qualityLabel = "1080p Crystal Reel (Full HD)",
                resolutionLabel = "1080x1920",
                height = 1080,
                fps = 60,
                formatType = FormatType.VIDEO,
                codec = "H.264 High Profile",
                approxFileSizeMb = 36.4,
                downloadUrl = SAMPLE_VIDEO_INSTA,
                isRecommended = true
            ),
            VideoFormat(
                id = "ig_720p",
                qualityLabel = "720p Standard Reel",
                resolutionLabel = "720x1280",
                height = 720,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264",
                approxFileSizeMb = 18.2,
                downloadUrl = SAMPLE_VIDEO_480P
            ),
            VideoFormat(
                id = "ig_audio",
                qualityLabel = "Extract Audio Track (M4A)",
                resolutionLabel = "Audio 256 kbps",
                height = 0,
                fps = 0,
                formatType = FormatType.AUDIO,
                codec = "AAC High Quality",
                approxFileSizeMb = 4.1,
                downloadUrl = SAMPLE_AUDIO_MP3
            )
        )

        return VideoInfo(
            url = url,
            platform = SupportedPlatform.INSTAGRAM,
            title = "Top 5 Hidden Italian Coastal Villages You Can Visit",
            author = "Traveler Diaries",
            authorHandle = "@traveler.diaries",
            durationSeconds = 54,
            formattedDuration = "0:54",
            thumbnailUri = "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800&q=80",
            viewCount = "2.1M views",
            likesCount = "310K likes",
            description = "Full 1080p vertical reel download with crystal audio master track.",
            formats = formats
        )
    }

    private fun buildRedditInfo(url: String): VideoInfo {
        val formats = listOf(
            VideoFormat(
                id = "reddit_1080p",
                qualityLabel = "1080p HD (Muxed Video + Audio)",
                resolutionLabel = "1920x1080",
                height = 1080,
                fps = 60,
                formatType = FormatType.VIDEO,
                codec = "H.264 / AAC Combined",
                approxFileSizeMb = 52.3,
                downloadUrl = SAMPLE_VIDEO_1080P,
                isRecommended = true
            ),
            VideoFormat(
                id = "reddit_720p",
                qualityLabel = "720p HD",
                resolutionLabel = "1280x720",
                height = 720,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264",
                approxFileSizeMb = 26.8,
                downloadUrl = SAMPLE_VIDEO_720P
            ),
            VideoFormat(
                id = "reddit_480p",
                qualityLabel = "480p SD",
                resolutionLabel = "854x480",
                height = 480,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "H.264",
                approxFileSizeMb = 13.9,
                downloadUrl = SAMPLE_VIDEO_480P
            )
        )

        return VideoInfo(
            url = url,
            platform = SupportedPlatform.REDDIT,
            title = "Time-lapse of Bioluminescent Waves in Southern California [r/NatureIsFuckingLit]",
            author = "u/DeepBlueOcean",
            authorHandle = "r/NatureIsFuckingLit",
            durationSeconds = 68,
            formattedDuration = "1:08",
            thumbnailUri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
            viewCount = "45.2K upvotes",
            likesCount = "1.8K comments",
            description = "Merged v.redd.it DASH video and audio streams into standard MP4 playable everywhere.",
            formats = formats
        )
    }

    private fun buildDirectInfo(url: String): VideoInfo {
        val formats = listOf(
            VideoFormat(
                id = "direct_orig",
                qualityLabel = "Original Quality (Direct Stream)",
                resolutionLabel = "Native Resolution",
                height = 1080,
                fps = 30,
                formatType = FormatType.VIDEO,
                codec = "MP4 / AAC",
                approxFileSizeMb = 25.0,
                downloadUrl = if (url.startsWith("http")) url else SAMPLE_VIDEO_1080P,
                isRecommended = true
            ),
            VideoFormat(
                id = "direct_audio",
                qualityLabel = "Audio Only (MP3)",
                resolutionLabel = "Audio 320 kbps",
                height = 0,
                fps = 0,
                formatType = FormatType.AUDIO,
                codec = "MP3 Audio",
                approxFileSizeMb = 5.0,
                downloadUrl = SAMPLE_AUDIO_MP3
            )
        )

        val fileName = url.substringAfterLast("/").substringBefore("?").ifEmpty { "Direct_Video.mp4" }

        return VideoInfo(
            url = url,
            platform = SupportedPlatform.DIRECT,
            title = fileName,
            author = "Web Stream",
            authorHandle = "Direct Source",
            durationSeconds = 120,
            formattedDuration = "2:00",
            thumbnailUri = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&q=80",
            viewCount = "-",
            likesCount = "-",
            description = "Direct video stream ready for local download.",
            formats = formats
        )
    }

    private fun parseYouTubeId(url: String): String {
        return when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("shorts/") -> url.substringAfter("shorts/").substringBefore("?")
            else -> "sample_id"
        }
    }
}
