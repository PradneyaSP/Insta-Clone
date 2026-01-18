package com.example.myinsta.model

import com.squareup.moshi.Json

interface LikeRequest

data class PostLikeRequest (
    @property:Json("post_id")
    val postId: String,
    @property:Json("like")
    val isLiked: Boolean
): LikeRequest

data class ReelLikeRequest (
    @property:Json("reel_id")
    val reelId: String,
    @property:Json("like")
    val isLiked: Boolean
) : LikeRequest
