package com.kars.pooledexo.controller

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kars.pooledexo.core.MediaSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Represents the different states a player can be in.
 * 
 * Maps to ExoPlayer's internal states to provide a simpler, abstracted interface.
 */
enum class PlayerState {
    /** Player has been instantiated but is not ready to play */
    IDLE,
    /** Player is buffering content */
    BUFFERING,
    /** Player is ready to play or is playing */
    READY,
    /** Playback has reached the end of the media */
    ENDED,
    /** Player is in an unknown state */
    UNKNOWN,
    ;

    companion object {
        /**
         * Converts an ExoPlayer playback state to a PlayerState.
         *
         * @param playbackState The playback state from ExoPlayer
         * @return The corresponding PlayerState
         */
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

/**
 * Controller for interacting with an ExoPlayer instance.
 * 
 * This class provides a simplified interface for controlling media playback
 * using an ExoPlayer instance from the pool.
 *
 * @property player The ExoPlayer instance being controlled
 * @property viewId The unique identifier for the view associated with this player
 * @property onReleasePlayer Callback to be invoked when the player is released
 */
class ExoPlayerController internal constructor(
    private val player: ExoPlayer,
    private val viewId: String,
    private val onReleasePlayer: (String) -> Unit,
) {
    private var playerListener: Player.Listener? = null

    /**
     * Returns the underlying ExoPlayer instance.
     *
     * @return The ExoPlayer instance managed by this controller
     */
    fun getPlayer(): ExoPlayer = player

    /**
     * Prepares the player with the specified media source.
     *
     * @param mediaSource The source of the media to be played
     */
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

    /**
     * Starts or resumes playback.
     */
    fun play() {
        player.play()
    }

    /**
     * Pauses playback.
     */
    fun pause() {
        player.pause()
    }

    /**
     * Seeks to the specified position in the current media item.
     *
     * @param positionMs The position to seek to, in milliseconds
     */
    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    /**
     * Registers a callback to be invoked when the media is ready to play.
     *
     * @param callback The callback to be invoked
     */
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

    /**
     * Returns a Flow that emits player state changes.
     *
     * @return A Flow of PlayerState values representing the player's state changes
     */
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

    /**
     * Releases the player back to the pool.
     * 
     * This should be called when the player is no longer needed.
     */
    fun release() {
        playerListener?.let { player.removeListener(it) }
        playerListener = null
        onReleasePlayer(viewId)
    }

    /**
     * Sets the player's volume.
     *
     * @param volume The volume level, where 0 is silent and 1 is full volume
     */
    fun setVolume(volume: Float) {
        player.volume = volume
    }

    /**
     * Whether the player is currently playing.
     */
    val isPlaying: Boolean
        get() = player.isPlaying

    /**
     * The current playback position in milliseconds.
     */
    val currentPosition: Long
        get() = player.currentPosition

    /**
     * The duration of the current media in milliseconds.
     */
    val duration: Long
        get() = player.duration
}
