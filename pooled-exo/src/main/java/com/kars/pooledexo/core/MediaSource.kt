package com.kars.pooledexo.core

sealed class MediaSource {
    data class Network(val url: String) : MediaSource()

    data class Asset(val assetPath: String) : MediaSource()

    data class File(val filePath: String) : MediaSource()
}
