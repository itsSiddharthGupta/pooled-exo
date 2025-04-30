package com.kars.pooledexo.controller

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kars.pooledexo.core.MediaSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

enum class PlayerState {
    IDLE,
    BUFFERING,
    READY,
    ENDED,
    UNKNOWN,
    ;

    companion object {
        fun fromPlaybackState(playbackState: Int): PlayerState =
            when (playbackState) {
                Player.STATE_IDLE -> IDLE
                Player.STATE_BUFFERING -> BUFFERING
                Player.STATE_READY -> READY
                Player.STATE_ENDED -> ENDED
                else -> UNKNOWN
            }
    }
}

class ExoPlayerController internal constructor(
    private val player: ExoPlayer,
    private val viewId: String,
    private val onReleasePlayer: (String) -> Unit,
) {
    private var playerListener: Player.Listener? = null

    fun getPlayer(): ExoPlayer = player

    fun prepare(mediaSource: MediaSource) {
        val mediaItem =
            when (mediaSource) {
                is MediaSource.Network -> MediaItem.fromUri(mediaSource.url)
                is MediaSource.Asset -> MediaItem.fromUri("asset:///${mediaSource.assetPath}")
                is MediaSource.File -> MediaItem.fromUri(mediaSource.filePath)
            }
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    fun onMediaReady(callback: () -> Unit) {
        val listener =
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_READY) {
                        callback()
                        player.removeListener(this)
                    }
                }
            }
        player.addListener(listener)
    }

    fun getPlayerEventsFlow() =
        callbackFlow {
            playerListener?.let { player.removeListener(it) }

            val listener =
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        trySend(PlayerState.fromPlaybackState(playbackState))
                    }
                }

            playerListener = listener
            player.addListener(listener)

            awaitClose {
                playerListener?.let { player.removeListener(it) }
                playerListener = null
            }
        }

    fun release() {
        playerListener?.let { player.removeListener(it) }
        playerListener = null
        onReleasePlayer(viewId)
    }

    fun setVolume(volume: Float) {
        player.volume = volume
    }

    val isPlaying: Boolean
        get() = player.isPlaying

    val currentPosition: Long
        get() = player.currentPosition

    val duration: Long
        get() = player.duration
}
