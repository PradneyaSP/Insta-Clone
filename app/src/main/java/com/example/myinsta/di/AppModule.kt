package com.example.myinsta.di

import android.app.Application
import com.example.myinsta.api.ApiService
import com.example.myinsta.data.AppDatabase
import com.example.myinsta.data.PostDao
import com.example.myinsta.data.ReelDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

// This defines that the class is a collection of dependencies
@Module
// This defines how long the dependencies live / lifetime (Ex: ActivityRetained, ViewModelScope)
@InstallIn(SingletonComponent::class)
object AppModule {

    // This defined the scope of the dependency (How many of this dependencies can be created)
    @Singleton
    // This is used mainly to inject dependencies whose classes are not accessible / are abstract
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(moshi: Moshi): Retrofit {
        val baseUrl = "https://dfbf9976-22e3-4bb2-ae02-286dfd0d7c42.mock.pstmn.io/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        val api: ApiService = retrofit.create(ApiService::class.java)
        return api
    }

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase {
        return AppDatabase.getDatabase(app)
    }

    @Singleton
    @Provides
    fun providePostDao(appDatabase: AppDatabase): PostDao{
        return appDatabase.postDao()
    }

    @Singleton
    @Provides
    fun provideReelDao(appDatabase: AppDatabase): ReelDao {
        return appDatabase.reelDao()
    }
}