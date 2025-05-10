package com.moviles.servitech.network

import android.content.Context
import com.moviles.servitech.common.Constants.API_BASE_URL
import com.moviles.servitech.network.services.ArticleService
import com.moviles.servitech.network.services.AuthService
import com.moviles.servitech.network.services.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitInstance {

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, networkStatusTracker: NetworkStatusTracker): OkHttpClient {
        val offlineInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!networkStatusTracker.isConnected.value) {
                request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=86400") // 1 day
                    .build()
            }
            chain.proceed(request)
        }

        val onlineInterceptor = Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            response.newBuilder()
                .header("Cache-Control", "public, max-age=60") // 1 minute
                .build()
        }

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(offlineInterceptor)
            .addNetworkInterceptor(onlineInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideArticleService(retrofit: Retrofit): ArticleService =
        retrofit.create(ArticleService::class.java)

}