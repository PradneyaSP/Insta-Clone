package com.example.myinsta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myinsta.model.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getAllPosts(): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateAllPosts(newPosts: List<Post>)

    @Query("UPDATE posts SET likedByUser = :isLiked AND likeCount = :newCount WHERE postId = :postId")
    fun updateLikeStatus(postId: String, isLiked: Boolean , newCount: Int)
}