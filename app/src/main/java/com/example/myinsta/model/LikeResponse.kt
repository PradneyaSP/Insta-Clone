package com.example.myinsta.model

import com.squareup.moshi.Json

data class LikeResponse(
    @property:Json(name = "status")
    val status: String,
    @property:Json(name = "message")
    val message: String,
    @property:Json(name = "timestamp")
    val timestamp: String
)