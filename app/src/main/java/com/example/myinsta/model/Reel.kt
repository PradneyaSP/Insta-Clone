package com.example.myinsta.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "reels")
data class Reel (
    @PrimaryKey
    @property:Json(name = "reel_id")
    val reelId: String,
    @property:Json(name = "user_name")
    val userName: String,
    @property:Json(name="user_image")
    val userImage: String,
    @property:Json(name="reel_video")
    val reelVideo: String,
    @property:Json(name="like_count")
    val likeCount: Int,
    @property:Json(name="liked_by_user")
    val likedByUser: Boolean
)

data class ReelsResponse (
    @property:Json("reels")
    val reels : List<Reel>
)