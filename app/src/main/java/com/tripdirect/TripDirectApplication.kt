package com.tripdirect

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.tripdirect.data.network.ApiClient
import okhttp3.OkHttpClient

/**
 * Custom Application class for TripDirect.
 * Configures Coil ImageLoader with a descriptive User-Agent so that
 * all open-licensed remote image sources (including Wikimedia Commons and Unsplash)
 * load reliably without HTTP 403 blocks.
 */
class TripDirectApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "TripDirect-Android/1.0 (contact@tripdirect.edu.in)")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
