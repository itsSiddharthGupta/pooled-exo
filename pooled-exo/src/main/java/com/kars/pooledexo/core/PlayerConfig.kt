package com.kars.pooledexo.core

private const val MB_IN_BYTES = 1024L * 1024L
private const val DEFAULT_CACHE_SIZE_MB = 50L

data class PlayerConfig(
    val cacheConfig: CacheConfig = CacheConfig(),
    val networkConfig: NetworkConfig = NetworkConfig(),
    val poolConfig: PoolConfig = PoolConfig(),
) {
    data class CacheConfig(
        val maxCacheSizeInBytes: Long = DEFAULT_CACHE_SIZE_MB * MB_IN_BYTES, // 512MB
        val cacheDirectoryName: String = "media_cache",
    )

    data class NetworkConfig(
        val connectTimeoutMs: Int = 15_000,
        val readTimeoutMs: Int = 15_000,
        val allowCrossProtocolRedirects: Boolean = true,
        val userAgent: String? = null,
    )

    data class PoolConfig(
        val maxPooledPlayers: Int = 3,
        val enablePlayerReuse: Boolean = true,
    )
}
