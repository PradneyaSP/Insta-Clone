package com.example.myinsta.api

import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "https://dfbf9976-22e3-4bb2-ae02-286dfd0d7c42.mock.pstmn.io/"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    var api: ApiService = retrofit.create(ApiService::class.java)
}