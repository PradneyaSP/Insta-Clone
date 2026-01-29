package com.example.myinsta.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myinsta.data.PostRepository
import com.example.myinsta.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val repository: PostRepository
) : ViewModel(){
    private val _feed = MutableLiveData<List<Post>>()
    val feed: LiveData<List<Post>> = _feed

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadFeed() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                _feed.value = repository.getPosts()
            } catch(e: Exception) {
                _error.value = "Error fetching the feed"
                Log.d("FeedViewModel","${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLikeStatus(updatedPost: Post){
        val currentFeed = _feed.value ?: return
        val updatedPosts = currentFeed.map {
            if(it.postId == updatedPost.postId) updatedPost else it
        }
        _feed.value = updatedPosts
    }
}