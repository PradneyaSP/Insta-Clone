package com.example.myinsta.data

import android.util.Log
import com.example.myinsta.api.ApiService
import com.example.myinsta.model.PostLikeRequest
import com.example.myinsta.model.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: ApiService,
    private val postDao: PostDao
) {
    suspend fun getPosts(): List<Post>{
        return try {
            val posts = apiService.getFeed().feed
            postDao.updateAllPosts(posts)
            posts
        } catch (e: Exception){
            Log.d("Network Error", "Fetching from Room Database")
            val posts = postDao.getAllPosts()
            posts
        }
    }

    suspend fun updateLikeStatus(post: Post){
        postDao.updateLikeStatus(post.postId, post.likedByUser, post.likeCount)

        try {
            val request = PostLikeRequest(post.postId, post.likedByUser)
            if(post.likedByUser)
                apiService.likePostOrReel(request)
            else
                apiService.dislikePostOrReel(request)
        } catch(e: Exception){
            Log.d("Network Error" , "Like Post API Error")
        }
    }
}