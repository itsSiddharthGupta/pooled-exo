# Pooled ExoPlayer Library

> **⚠️ WORK IN PROGRESS ⚠️** - This library is currently under active development and the API may change.

A lightweight Android library that manages a pool of ExoPlayer instances to improve performance and reduce resource usage in apps that require multiple media players.

## Overview

Pooled-ExoPlayer addresses the common challenge of managing multiple ExoPlayer instances in applications that display multiple videos simultaneously (like social feeds, carousels, or grid layouts). Instead of creating and destroying ExoPlayer instances, which is resource-intensive, this library maintains a pool of reusable players.

## Features

- **Player Pooling**: Efficiently reuse ExoPlayer instances instead of creating new ones
- **Media Caching**: Built-in media caching to improve playback performance
- **Simple API**: Clean, intuitive interface to manage ExoPlayer instances
- **Customizable**: Flexible configuration options for cache size, network settings, and pool management
- **Lifecycle Aware**: Properly manage resources based on view lifecycle

## Installation

*Coming soon - Library will be published to Maven Central*

For now, you can include the library in your project by:

```groovy
// In your project's settings.gradle.kts
include(":pooled-exo")

// In your app's build.gradle.kts
implementation(project(":pooled-exo"))
```

## Usage

### Basic Setup

```kotlin
// Initialize the ExoPlayer manager
val exoPlayerManager = ExoPlayerManager(context)
exoPlayerManager.initialize()

// Create a player controller
val controller = exoPlayerManager.createPlayerController("video_1")

// Prepare and play media
controller.prepare(MediaSource.Network("https://example.com/video.mp4"))
controller.play()

// Release when done
controller.release()

// Release all resources when your component is destroyed
exoPlayerManager.release()
```

### Configuration

```kotlin
val config = PlayerConfig(
    cacheConfig = PlayerConfig.CacheConfig(
        maxCacheSizeInBytes = 100 * 1024 * 1024, // 100MB
        cacheDirectoryName = "my_media_cache"
    ),
    networkConfig = PlayerConfig.NetworkConfig(
        connectTimeoutMs = 10_000,
        readTimeoutMs = 10_000,
        userAgent = "MyApp/1.0"
    ),
    poolConfig = PlayerConfig.PoolConfig(
        maxPooledPlayers = 5,
        enablePlayerReuse = true
    )
)

exoPlayerManager.initialize(config)
```

### Different Media Sources

```kotlin
// Network media
controller.prepare(MediaSource.Network("https://example.com/video.mp4"))

// Local file
controller.prepare(MediaSource.File("/storage/videos/local-video.mp4"))

// App asset
controller.prepare(MediaSource.Asset("videos/asset-video.mp4"))
```

### Listening to Player Events

```kotlin
controller.getPlayerEventsFlow().collect { state ->
    when (state) {
        PlayerState.READY -> // Player is ready
        PlayerState.BUFFERING -> // Player is buffering
        PlayerState.ENDED -> // Playback has ended
        PlayerState.IDLE -> // Player is idle
        PlayerState.UNKNOWN -> // Unknown state
    }
}
```

## Dependencies

This library relies on:
- ExoPlayer (androidx.media3:media3-exoplayer)
- Kotlin Coroutines

## License

*License details coming soon*

## Contributing

Contributions are welcome! As this project is still a work in progress, please open an issue to discuss any changes you'd like to make.

---

## Roadmap

- [x] Basic player pooling functionality
- [x] Media caching
- [ ] Improved error handling
- [ ] UI components for common video player layouts
- [ ] Testing across more devices
- [ ] Performance optimizations
- [ ] Publish to Maven Central 