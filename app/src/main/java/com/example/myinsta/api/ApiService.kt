package com.example.myinsta.api

import com.example.myinsta.model.FeedResponse
import com.example.myinsta.model.LikeRequest
import com.example.myinsta.model.LikeResponse
import com.example.myinsta.model.ReelsResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("user/feed")
    suspend fun getFeed(): FeedResponse

    @POST("user/like")
    suspend fun likePostOrReel(@Body likeRequest: LikeRequest): LikeResponse

    @DELETE("user/dislike")
    suspend fun dislikePostOrReel(@Body dislikeRequest: LikeRequest): LikeResponse

    @GET("user/reels")
    suspend fun getReels() : ReelsResponse
}