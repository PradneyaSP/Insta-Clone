package com.example.myinsta.model

import com.squareup.moshi.Json

data class Post (
    @property:Json(name = "post_id")
    val postId: String,
    @property:Json(name = "user_name")
    val userName: String,
    @property:Json(name="user_image")
    val userImage: String,
    @property:Json(name="post_image")
    val postImage: String,
    @property:Json(name="like_count")
    val likeCount: Int,
    @property:Json(name="liked_by_user")
    val likedByUser: Boolean
)