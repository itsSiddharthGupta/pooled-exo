package com.kars.pooledexo.core

private const val MB_IN_BYTES = 1024L * 1024L
private const val DEFAULT_CACHE_SIZE_MB = 50L

/**
 * Configuration class for ExoPlayer instances in the pool.
 * 
 * This class contains all configuration options for ExoPlayers managed by the pool,
 * including cache settings, network options, and pool management parameters.
 *
 * @property cacheConfig Configuration for media caching
 * @property networkConfig Configuration for network requests
 * @property poolConfig Configuration for the player pool behavior
 */
data class PlayerConfig(
    val cacheConfig: CacheConfig = CacheConfig(),
    val networkConfig: NetworkConfig = NetworkConfig(),
    val poolConfig: PoolConfig = PoolConfig(),
) {
    /**
     * Configuration for media caching.
     *
     * @property maxCacheSizeInBytes Maximum size of the media cache in bytes, defaults to 50MB
     * @property cacheDirectoryName Name of the directory where media will be cached
     */
    data class CacheConfig(
        val maxCacheSizeInBytes: Long = DEFAULT_CACHE_SIZE_MB * MB_IN_BYTES, // 512MB
        val cacheDirectoryName: String = "media_cache",
    )

    /**
     * Configuration for network requests.
     *
     * @property connectTimeoutMs Connection timeout in milliseconds
     * @property readTimeoutMs Read timeout in milliseconds
     * @property allowCrossProtocolRedirects Whether to allow cross-protocol redirects
     * @property userAgent Custom user agent string for network requests, if null, system default is used
     */
    data class NetworkConfig(
        val connectTimeoutMs: Int = 15_000,
        val readTimeoutMs: Int = 15_000,
        val allowCrossProtocolRedirects: Boolean = true,
        val userAgent: String? = null,
    )

    /**
     * Configuration for the player pool behavior.
     *
     * @property maxPooledPlayers Maximum number of players to keep in the pool
     * @property enablePlayerReuse Whether to reuse players or create new ones when needed
     */
    data class PoolConfig(
        val maxPooledPlayers: Int = 3,
        val enablePlayerReuse: Boolean = true,
    )
}
