package com.kars.pooledexo

import android.content.Context
import com.kars.pooledexo.controller.ExoPlayerController
import com.kars.pooledexo.core.PlayerConfig
import com.kars.pooledexo.internal.ExoPlayerPool
import com.kars.pooledexo.internal.cache.MediaCache

class ExoPlayerManager(private val context: Context) {
    private var playerPool: ExoPlayerPool? = null
    private val mediaCache: MediaCache by lazy {
        MediaCache(context, config.cacheConfig)
    }
    private var config: PlayerConfig = PlayerConfig()

    fun initialize(config: PlayerConfig = PlayerConfig()) {
        this.config = config
        playerPool =
            ExoPlayerPool(
                context = context,
                mediaCache = mediaCache,
                config = config,
            )
    }

    fun createPlayerController(viewId: String): ExoPlayerController {
        val pool = checkInitialization()
        return ExoPlayerController(
            player = pool.acquirePlayer(viewId),
            viewId = viewId,
        ) { id ->
            pool.releasePlayer(id)
        }
    }

    fun release() {
        playerPool?.clearPool()
        mediaCache.release()
        playerPool = null
    }

    private fun checkInitialization(): ExoPlayerPool {
        return playerPool?.let {
            playerPool
        } ?: error("ExoPlayerManager must be initialized before use")
    }
}
