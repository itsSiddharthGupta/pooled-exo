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

@OptIn(UnstableApi::class)
internal class PlayerFactory(
    private val context: Context,
    private val mediaCache: MediaCache,
    private val config: PlayerConfig,
) {
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
