package com.moviles.servitech.network

import android.content.Context
import com.moviles.servitech.common.Constants.API_BASE_URL
import com.moviles.servitech.network.services.ArticleApiService
import com.moviles.servitech.network.services.AuthApiService
import com.moviles.servitech.network.services.RepairRequestApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import javax.inject.Singleton

/**
 * RetrofitInstance provides the Retrofit instance with OkHttpClient
 * configured for caching and network status handling.
 *
 * This module is installed in the SingletonComponent,
 * ensuring a single instance is used throughout the application.
 *
 * The OkHttpClient uses a cache of 10 MB and includes interceptors
 * to handle offline caching and network status.
 *
 * The offline interceptor checks if the device is connected to the network.
 * If not, it sets the cache control to allow stale data for 1 day.
 *
 * The online interceptor sets the cache control to allow fresh data
 * for 1 minute when the device is connected to the network.
 *
 * This setup ensures efficient network usage and provides a seamless
 * user experience by allowing the app to function
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    /**
     * Provides a Cache instance for OkHttpClient.
     * The cache size is set to 10 MB.
     *
     * @param context Application context to access cache directory.
     * @return [Cache] instance.
     */
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    /**
     * Provides an OkHttpClient instance with caching and interceptors.
     * The client is configured to handle offline caching and network status.
     *
     * @param cache Cache instance for OkHttpClient.
     * @param networkStatusTracker NetworkStatusTracker to check connectivity.
     * @return [OkHttpClient] instance.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, networkStatusTracker: NetworkStatusTracker): OkHttpClient {
        // This interceptor handles offline caching
        val offlineInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!networkStatusTracker.isConnected.value) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=86400") // 1 day
                    .build()
            }
            chain.proceed(request)
        }

        // This interceptor handles online caching
        val onlineInterceptor = Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60") // 1 minute
                .build()
        }

        // This interceptor adds the Accept-Language header to requests
        val languageInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept-Language", Locale.getDefault().language)
                .build()
            chain.proceed(request)
        }

        // This interceptor adds logging for debugging network issues
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(cache) // Set the cache for OkHttpClient
            .addInterceptor(offlineInterceptor) // Add the offline interceptor
            .addInterceptor(languageInterceptor) // Add the language interceptor
            .addNetworkInterceptor(onlineInterceptor) // Add the online interceptor
            .addInterceptor(loggingInterceptor) // Add the logging interceptor
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // 30 seconds connection timeout
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // 60 seconds read timeout
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS) // 60 seconds write timeout
            .build() // Build the OkHttpClient
    }

    /**
     * Provides a Retrofit instance configured with the base URL and OkHttpClient.
     * The Retrofit instance uses GsonConverterFactory for JSON serialization.
     *
     * @param okHttpClient OkHttpClient instance for network requests.
     * @return [Retrofit] instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * Provides the AuthApiService for authentication-related API calls.
     * This service is created using the Retrofit instance.
     *
     * @param retrofit [Retrofit] instance for creating API services.
     * @return [AuthApiService] instance for authentication API calls.
     */
    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    /**
     * Provides the ArticleApiService for article-related API calls.
     * This service is created using the Retrofit instance.
     *
     * @param retrofit [Retrofit] instance for creating API services.
     * @return [ArticleApiService] instance for article API calls.
     */
    @Provides
    @Singleton
    fun provideArticleService(retrofit: Retrofit): ArticleApiService =
        retrofit.create(ArticleApiService::class.java)

    /**
     * Provides the RepairRequestApiService for repair request-related API calls.
     * This service is created using the Retrofit instance.
     *
     * This service handles operations such as fetching, creating, updating,
     * and deleting repair requests.
     *
     * @param retrofit [Retrofit] instance for creating API services.
     * @return [RepairRequestApiService] instance for repair request API calls.
     */
    @Provides
    @Singleton
    fun provideRepairRequestService(retrofit: Retrofit): RepairRequestApiService =
        retrofit.create(RepairRequestApiService::class.java)

}