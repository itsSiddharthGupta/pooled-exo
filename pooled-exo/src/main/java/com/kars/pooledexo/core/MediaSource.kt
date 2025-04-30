package com.kars.pooledexo.core

/**
 * Represents the source of media content for playback.
 * 
 * This sealed class defines the different types of media sources that can be used
 * with the ExoPlayer, including network URLs, asset files, and local files.
 */
sealed class MediaSource {
    /**
     * Media source for content accessed over the network.
     *
     * @property url The URL of the media content
     */
    data class Network(val url: String) : MediaSource()

    /**
     * Media source for content from the app's assets.
     *
     * @property assetPath The path to the asset within the app's assets folder
     */
    data class Asset(val assetPath: String) : MediaSource()

    /**
     * Media source for content from the local file system.
     *
     * @property filePath The path to the file on the local file system
     */
    data class File(val filePath: String) : MediaSource()
}
