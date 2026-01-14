package com.example.myinsta.api

import com.example.myinsta.model.LikeRequest
import com.example.myinsta.model.LikeResponse
import com.example.myinsta.model.Post
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("user/feed")
    suspend fun getFeed(): List<Post>

    @POST("user/like")
    suspend fun likePost(@Body likeRequest: LikeRequest): LikeResponse

    @DELETE("user/dislike")
    suspend fun dislikePost(@Body dislikeRequest: LikeRequest): LikeResponse
}