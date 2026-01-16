package com.example.myinsta.model

import com.squareup.moshi.Json

data class LikeRequest (
    @property:Json("post_id")
    val postId: String,
    @property:Json("like")
    val isLiked: Boolean
)
