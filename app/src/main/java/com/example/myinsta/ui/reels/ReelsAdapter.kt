package com.example.myinsta.ui.reels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myinsta.R
import com.example.myinsta.data.ReelsRepository
import com.example.myinsta.model.Reel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale.getDefault

class ReelsAdapter(
    val reels: MutableList<Reel>,
    val repository: ReelsRepository,
    val coroutineScope: CoroutineScope,
    val viewModel: ReelsViewModel,
    val getExoPlayer: () -> ExoPlayer,
) : RecyclerView.Adapter<ReelsAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.tvUsername)
        val userImage: ImageView = view.findViewById(R.id.ivAvatar)
        val reelVideoPlayer: PlayerView = view.findViewById(R.id.exoReelScreen)
        val likeCount: TextView = view.findViewById(R.id.tvLikeCountReel)
        val btnLikeButton: ImageButton = view.findViewById(R.id.btnLikeReelButton)
        val btnMuteButton: ImageButton = view.findViewById(R.id.btnMuteReelButton)
        var player: ExoPlayer? = null
        var isMuted: Boolean = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reel, parent, false)
        return ReelViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReelViewHolder,
        position: Int
    ) {
        val currentReel = reels[position]
        holder.apply {
            val username = currentReel.userName.split("_").joinToString(" ") { s ->
                s.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        getDefault()
                    ) else it.toString()
                }
            }
            userName.text = username
            likeCount.text = currentReel.likeCount.toString()

            val mediaItem = MediaItem.fromUri(currentReel.reelVideo)

            // Release previous player if exists and detach from PlayerView
            player?.let { oldPlayer ->
                oldPlayer.stop()
                oldPlayer.release()
            }
            reelVideoPlayer.player = null
            
            // Create new player
            player = getExoPlayer()

            // Hide video controls
            reelVideoPlayer.useController = false
            
            player?.let { p ->
                p.setMediaItem(mediaItem)
                reelVideoPlayer.player = p
                p.repeatMode = Player.REPEAT_MODE_ONE
                p.volume = 0f // Muted by default
                isMuted = true
                p.prepare()
                // Don't play here - will be controlled by ViewPager2 page change callback
            }

            if (currentReel.likedByUser)
                btnLikeButton.setImageResource(R.drawable.ic_heart_filled_32)
            else
                btnLikeButton.setImageResource(R.drawable.ic_heart_32)

            btnLikeButton.setOnClickListener {
                val updatedReel = if (currentReel.likedByUser) {
                    currentReel.copy(likedByUser = false, likeCount = currentReel.likeCount - 1)
                } else {
                    currentReel.copy(likedByUser = true, likeCount = currentReel.likeCount + 1)
                }
                coroutineScope.launch {
                    repository.updateLikeStatus(updatedReel)
                }
                viewModel.updateReelLikeStatus(updatedReel)
            }

            // Mute/Unmute button functionality
            btnMuteButton.setOnClickListener {
                isMuted = !isMuted
                player?.volume = if (isMuted) 0f else 1f
                btnMuteButton.setImageResource(
                    if (isMuted) R.drawable.ic_volume_off_32 else R.drawable.ic_volume_32
                )
            }
            
            // Set initial mute button icon
            btnMuteButton.setImageResource(R.drawable.ic_volume_off_32)
        }

        Glide.with(holder.itemView).load(currentReel.userImage).into(holder.userImage)

    }

    override fun getItemCount() = reels.count()

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
        holder.player?.pause()
        holder.player?.release()
        holder.player = null
        holder.reelVideoPlayer.player = null
    }

}