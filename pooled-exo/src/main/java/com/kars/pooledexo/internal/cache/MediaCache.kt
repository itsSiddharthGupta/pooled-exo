package com.kars.pooledexo.internal.cache

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.kars.pooledexo.core.PlayerConfig
import java.io.File

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

    fun getCache(): SimpleCache = checkNotNull(cache) { "Cache has been released" }

    fun release() {
        cache?.release()
        cache = null
    }
}
