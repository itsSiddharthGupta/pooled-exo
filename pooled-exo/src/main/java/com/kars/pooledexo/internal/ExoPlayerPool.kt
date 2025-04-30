package com.kars.pooledexo.internal

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.kars.pooledexo.core.PlayerConfig
import com.kars.pooledexo.internal.cache.MediaCache
import com.kars.pooledexo.internal.factory.PlayerFactory

/**
 * Internal pool manager for ExoPlayer instances.
 * 
 * This class maintains a pool of ExoPlayer instances to minimize the overhead of
 * creating and destroying players. It manages both available (unused) players and
 * active (in-use) players.
 *
 * @property config Configuration that controls pool behavior
 */
internal class ExoPlayerPool(
    context: Context,
    mediaCache: MediaCache,
    private val config: PlayerConfig,
) {
    private val availablePlayers = mutableListOf<ExoPlayer>()
    private val activePlayerMap = mutableMapOf<String, ExoPlayer>()
    private val playerFactory = PlayerFactory(context, mediaCache, config)

    /**
     * Acquires an ExoPlayer instance for use.
     * 
     * Based on configuration, this will either reuse an existing player,
     * retrieve one from the pool of available players, or create a new one.
     *
     * @param viewId Unique identifier for the view using this player
     * @return An ExoPlayer instance ready for use
     */
    fun acquirePlayer(viewId: String): ExoPlayer {
        if (config.poolConfig.enablePlayerReuse) {
            val existingPlayer = activePlayerMap[viewId]

            val player =
                existingPlayer
                    ?: if (availablePlayers.isNotEmpty()) {
                        availablePlayers.removeAt(0)
                    } else {
                        playerFactory.create()
                    }
            activePlayerMap[viewId] = player
            return player
        }

        val player = playerFactory.create()
        activePlayerMap[viewId] = player
        return player
    }

    /**
     * Releases a player back to the pool or destroys it.
     * 
     * When a player is no longer needed, this method should be called to properly
     * manage resources. Based on configuration, the player will either be added
     * back to the available pool or released completely.
     *
     * @param viewId The identifier of the view that was using the player
     */
    fun releasePlayer(viewId: String) {
        activePlayerMap.remove(viewId)?.let { player ->
            player.stop()
            player.clearMediaItems()

            if (config.poolConfig.enablePlayerReuse &&
                availablePlayers.size < config.poolConfig.maxPooledPlayers
            ) {
                availablePlayers.add(player)
            } else {
                player.release()
            }
        }
    }

    /**
     * Clears all players from the pool.
     * 
     * Releases all players, both available and active, and clears the pool.
     * This should be called when the pool is no longer needed.
     */
    fun clearPool() {
        availablePlayers.forEach { it.release() }
        availablePlayers.clear()
        activePlayerMap.values.forEach { it.release() }
        activePlayerMap.clear()
    }
}
