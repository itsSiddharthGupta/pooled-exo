package com.kars.pooledexo.internal

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.kars.pooledexo.core.PlayerConfig
import com.kars.pooledexo.internal.cache.MediaCache
import com.kars.pooledexo.internal.factory.PlayerFactory

internal class ExoPlayerPool(
    context: Context,
    mediaCache: MediaCache,
    private val config: PlayerConfig,
) {
    private val availablePlayers = mutableListOf<ExoPlayer>()
    private val activePlayerMap = mutableMapOf<String, ExoPlayer>()
    private val playerFactory = PlayerFactory(context, mediaCache, config)

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

    fun clearPool() {
        availablePlayers.forEach { it.release() }
        availablePlayers.clear()
        activePlayerMap.values.forEach { it.release() }
        activePlayerMap.clear()
    }
}
