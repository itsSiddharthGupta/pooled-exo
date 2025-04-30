package com.kars.pooledexo

import android.content.Context
import com.kars.pooledexo.controller.ExoPlayerController
import com.kars.pooledexo.core.PlayerConfig
import com.kars.pooledexo.internal.ExoPlayerPool
import com.kars.pooledexo.internal.cache.MediaCache

/**
 * Manager class for handling ExoPlayer instances in a pool.
 * 
 * This class manages the lifecycle of ExoPlayer instances and provides a way to acquire
 * and release players as needed. It initializes with a PlayerConfig and manages the MediaCache.
 *
 * @property context The Android context used to create ExoPlayer instances
 */
class ExoPlayerManager(private val context: Context) {
    private var playerPool: ExoPlayerPool? = null
    private val mediaCache: MediaCache by lazy {
        MediaCache(context, config.cacheConfig)
    }
    private var config: PlayerConfig = PlayerConfig()

    /**
     * Initializes the ExoPlayerManager with the given configuration.
     * 
     * This must be called before any players can be created or acquired.
     *
     * @param config The configuration for all players in the pool, defaults to [PlayerConfig]
     */
    fun initialize(config: PlayerConfig = PlayerConfig()) {
        this.config = config
        playerPool =
            ExoPlayerPool(
                context = context,
                mediaCache = mediaCache,
                config = config,
            )
    }

    /**
     * Creates and returns a new ExoPlayerController.
     * 
     * The controller provides an interface for controlling a player instance
     * acquired from the pool.
     *
     * @param viewId A unique identifier for the view associated with this player
     * @return An ExoPlayerController associated with an acquired player
     * @throws IllegalStateException if [initialize] hasn't been called
     */
    fun createPlayerController(viewId: String): ExoPlayerController {
        val pool = checkInitialization()
        return ExoPlayerController(
            player = pool.acquirePlayer(viewId),
            viewId = viewId,
        ) { id ->
            pool.releasePlayer(id)
        }
    }

    /**
     * Releases all resources held by this manager.
     * 
     * This clears the player pool and releases the media cache.
     * The manager must be re-initialized before use after calling this method.
     */
    fun release() {
        playerPool?.clearPool()
        mediaCache.release()
        playerPool = null
    }

    /**
     * Checks if initialization has been performed.
     *
     * @return The non-null ExoPlayerPool instance
     * @throws IllegalStateException if the pool hasn't been initialized
     */
    private fun checkInitialization(): ExoPlayerPool {
        return playerPool?.let {
            playerPool
        } ?: error("ExoPlayerManager must be initialized before use")
    }
}
