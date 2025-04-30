package com.kars.pooledexo.internal.cache

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.kars.pooledexo.core.PlayerConfig
import java.io.File

/**
 * Manages the media cache for ExoPlayer instances.
 * 
 * This internal class creates and maintains a SimpleCache instance
 * for caching media content according to the specified configuration.
 *
 * @property context The Android context used to determine cache directory
 * @property config Cache configuration options including size and directory
 */
@OptIn(UnstableApi::class)
internal class MediaCache(
    context: Context,
    config: PlayerConfig.CacheConfig,
) {
    private var cache: SimpleCache? = null

    init {
        val cacheDir = File(context.cacheDir, config.cacheDirectoryName)
        cache =
            SimpleCache(
                cacheDir,
                LeastRecentlyUsedCacheEvictor(config.maxCacheSizeInBytes),
            )
    }

    /**
     * Returns the SimpleCache instance for use with ExoPlayer's CacheDataSource.
     *
     * @return The SimpleCache instance
     * @throws IllegalStateException if the cache has been released
     */
    fun getCache(): SimpleCache = checkNotNull(cache) { "Cache has been released" }

    /**
     * Releases the media cache resources.
     * 
     * This should be called when the cache is no longer needed.
     * After calling this method, the cache cannot be used again.
     */
    fun release() {
        cache?.release()
        cache = null
    }
}
