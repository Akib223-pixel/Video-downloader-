package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.downloader.MediaExtractor
import com.example.data.model.SupportedPlatform
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OmniDownloader", appName)
    }

    @Test
    fun `detect platform from URLs accurately`() {
        assertEquals(SupportedPlatform.YOUTUBE, SupportedPlatform.detectFromUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals(SupportedPlatform.YOUTUBE, SupportedPlatform.detectFromUrl("https://youtu.be/dQw4w9WgXcQ"))
        assertEquals(SupportedPlatform.YOUTUBE, SupportedPlatform.detectFromUrl("https://youtube.com/shorts/sample"))
        assertEquals(SupportedPlatform.TIKTOK, SupportedPlatform.detectFromUrl("https://www.tiktok.com/@creator/video/12345"))
        assertEquals(SupportedPlatform.X_TWITTER, SupportedPlatform.detectFromUrl("https://x.com/tech/status/987654"))
        assertEquals(SupportedPlatform.X_TWITTER, SupportedPlatform.detectFromUrl("https://twitter.com/news/status/112233"))
        assertEquals(SupportedPlatform.INSTAGRAM, SupportedPlatform.detectFromUrl("https://www.instagram.com/reel/abcdefg"))
        assertEquals(SupportedPlatform.REDDIT, SupportedPlatform.detectFromUrl("https://reddit.com/r/videos/comments/abc1234"))
    }

    @Test
    fun `extract video metadata provides multi-resolution formats`() = runBlocking {
        val result = MediaExtractor.extract("https://youtube.com/watch?v=sample_video")
        assertTrue(result.isSuccess)
        val info = result.getOrNull()
        assertNotNull(info)
        assertEquals(SupportedPlatform.YOUTUBE, info!!.platform)
        assertTrue("Should have formats including HD", info.formats.isNotEmpty())
        assertTrue("Has 1080p format", info.formats.any { it.height == 1080 })
    }
}
