package com.example.myinsta.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myinsta.model.Reel

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels")
    suspend fun getAllReels() : List<Reel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllReels(newReels: List<Reel>)

    @Query("UPDATE reels SET likedByUser = :isLiked, likeCount = :newCount WHERE reelId = :reelId")
    suspend fun updateReelLikeStatus(reelId: String, isLiked: Boolean, newCount: Int)
}