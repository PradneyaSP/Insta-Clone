package com.example.myinsta.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myinsta.R
import com.example.myinsta.data.PostRepository
import com.example.myinsta.model.Post
import java.util.Locale.getDefault
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class FeedAdapter(
    val posts: MutableList<Post>,
    private val repository: PostRepository,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<FeedAdapter.PostViewHolder>(){

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val ivAvatar : ImageView= view.findViewById(R.id.ivAvatar)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val ivPostImage: ImageView = view.findViewById(R.id.ivPostImage)
        val btnLikeButton: ImageButton = view.findViewById(R.id.btnLikeButton)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        return PostViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val currentPost = posts[position]
        holder.apply {
            val username = currentPost.userName.split("_").joinToString(" ") { s ->
                s.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        getDefault()
                    ) else it.toString()
                }
            }
            tvUsername.text = username

            tvLikeCount.text = "${currentPost.likeCount} likes"

            if(currentPost.likedByUser)
                btnLikeButton.setImageResource(R.drawable.ic_heart_filled_32)
            else
                btnLikeButton.setImageResource(R.drawable.ic_heart_32)

            btnLikeButton.setOnClickListener {
                val updatedPost = if(currentPost.likedByUser){
                    currentPost.copy(likedByUser = false, likeCount = currentPost.likeCount-1)
                } else{
                    currentPost.copy(likedByUser = true, likeCount = currentPost.likeCount+1)
                }
                coroutineScope.launch {
                    repository.updateLikeStatus(updatedPost)
                }
                posts[position] = updatedPost
                notifyItemChanged(position)
            }
        }

        Glide.with(holder.itemView).load(currentPost.userImage).into(holder.ivAvatar)
        Glide.with(holder.itemView).load(currentPost.postImage).into(holder.ivPostImage)

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = posts.size
}