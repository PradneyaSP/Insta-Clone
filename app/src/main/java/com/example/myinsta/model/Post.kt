package com.example.myinsta.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "posts")
data class Post (
    @PrimaryKey
    @property:Json(name = "post_id")
    val postId: String,
    @property:Json(name = "user_name")
    val userName: String,
    @property:Json(name="user_image")
    val userImage: String,
    @property:Json(name="post_image")
    val postImage: String,
    @property:Json(name="like_count")
    var likeCount: Int,
    @property:Json(name="liked_by_user")
    var likedByUser: Boolean
)

data class FeedResponse (
    @property:Json("feed")
    val feed : List<Post>
)