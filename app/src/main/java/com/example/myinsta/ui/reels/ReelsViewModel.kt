package com.example.myinsta.ui.reels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.myinsta.data.PostRepository
import com.example.myinsta.data.ReelsRepository
import com.example.myinsta.model.Reel
import com.example.myinsta.ui.feed.FeedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val repository: ReelsRepository
) : ViewModel() {
    private val _reels = MutableLiveData<List<Reel>>()
    val reels: LiveData<List<Reel>> = _reels

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadReels() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                _reels.value = repository.getReels()
            } catch (e: Exception) {
                _error.value = "Couldn't load reels!"
                e.printStackTrace()
                Log.d("Reels Error", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReelLikeStatus(updatedReel: Reel) {
        val currentReels = _reels.value ?: return
        val updatedReels =
            currentReels.map { if (it.reelId == updatedReel.reelId) updatedReel else it }
        _reels.value = updatedReels
    }
}