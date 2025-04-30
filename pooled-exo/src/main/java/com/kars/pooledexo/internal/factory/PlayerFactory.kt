package com.kars.pooledexo.internal.factory

import android.content.Context
import android.media.MediaCodec
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.kars.pooledexo.core.PlayerConfig
import com.kars.pooledexo.internal.cache.MediaCache

/**
 * Factory for creating ExoPlayer instances with consistent configuration.
 * 
 * This internal class handles the creation of ExoPlayer instances with appropriate
 * configuration for networking, caching, and playback behavior.
 *
 * @property context The Android context used to create the ExoPlayer
 * @property mediaCache The media cache instance for caching media data
 * @property config Configuration options for the players
 */
@OptIn(UnstableApi::class)
internal class PlayerFactory(
    private val context: Context,
    private val mediaCache: MediaCache,
    private val config: PlayerConfig,
) {
    /**
     * Creates a new ExoPlayer instance with the configured settings.
     * 
     * This sets up the player with appropriate HTTP data source, caching,
     * and default playback settings according to the configuration.
     *
     * @return A new ExoPlayer instance ready for use
     */
    fun create(): ExoPlayer {
        val httpDataSourceFactory =
            DefaultHttpDataSource.Factory()
                .setConnectTimeoutMs(config.networkConfig.connectTimeoutMs)
                .setReadTimeoutMs(config.networkConfig.readTimeoutMs)
                .setAllowCrossProtocolRedirects(config.networkConfig.allowCrossProtocolRedirects)
                .apply {
                    config.networkConfig.userAgent?.let { setUserAgent(it) }
                }

        val cacheDataSourceFactory =
            CacheDataSource.Factory()
                .setCache(mediaCache.getCache())
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .build()
            .apply {
                playWhenReady = true
                videoScalingMode = MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT
                volume = 0f
                repeatMode = ExoPlayer.REPEAT_MODE_ALL
            }
    }
}
