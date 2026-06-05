package com.sunday.pokemontest.di

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.apollographql.cache.normalized.memory.MemoryCacheFactory
import com.apollographql.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo.network.okHttpClient
import com.sunday.pokemontest.cache.Cache.cache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            // Connection timeout
            .connectTimeout(30, TimeUnit.SECONDS)
            // Read timeout
            .readTimeout(30, TimeUnit.SECONDS)
            // Write timeout
            .writeTimeout(30, TimeUnit.SECONDS)
            // Entire call timeout
            .callTimeout(60, TimeUnit.SECONDS)
            //Logging Interceptor
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ApolloClient {
        val sqlCacheFactory = SqlNormalizedCacheFactory(
            context, "pokemon_apollo.db"
        )
        val memoryCacheFactory = MemoryCacheFactory(
            5 * 1024 * 1024
        ) // 5MB
        return ApolloClient.Builder()
            .serverUrl("https://beta.pokeapi.co/graphql/v1beta")
            .okHttpClient(okHttpClient)
            .cache(memoryCacheFactory.chain(sqlCacheFactory))
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .build()
    }
}