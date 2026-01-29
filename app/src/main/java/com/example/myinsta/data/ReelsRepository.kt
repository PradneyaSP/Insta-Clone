package com.example.myinsta.data

import android.util.Log
import com.example.myinsta.api.ApiService
import com.example.myinsta.model.Reel
import com.example.myinsta.model.ReelLikeRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReelsRepository @Inject constructor(
    private val api: ApiService,
    private val reelsDao: ReelDao
) {
    suspend fun getReels() : List<Reel> {
        return try {
            val reels = api.getReels().reels
            reelsDao.updateAllReels(reels)
            reels
        } catch (e: Exception){
            Log.d("Network Error", "Fetching Reels from Room Database")
            reelsDao.getAllReels()
        }
    }

    suspend fun updateLikeStatus(reel: Reel) {
        reelsDao.updateReelLikeStatus(reel.reelId, reel.likedByUser, reel.likeCount)
        try {
            val request = ReelLikeRequest(reel.reelId, reel.likedByUser)
            if(reel.likedByUser)
                api.likePostOrReel(request)
            else
                api.dislikePostOrReel(request)
        } catch (e: Exception) {
            Log.d("Network Error" , "Like Post API Error")
        }
    }
}