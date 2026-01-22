package com.example.myinsta.ui.reels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myinsta.R
import com.example.myinsta.api.RetrofitInstance
import com.example.myinsta.data.AppDatabase
import com.example.myinsta.data.ReelsRepository
import com.example.myinsta.databinding.FragmentReelsBinding
import com.example.myinsta.model.Reel

class ReelsFragment: Fragment() {

    private var _binding: FragmentReelsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReelsAdapter
    private lateinit var reelsViewModel: ReelsViewModel
    private var currentPosition: Int = 0
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = RetrofitInstance.api
        val reelsDao = AppDatabase.getDatabase(requireContext()).reelDao()
        val repository: ReelsRepository = ReelsRepository(api, reelsDao)

        val factory = ReelsViewModelFactory(repository)
        reelsViewModel = ViewModelProvider(this, factory)[ReelsViewModel::class.java]

        adapter = ReelsAdapter(mutableListOf(), repository, lifecycleScope, reelsViewModel) {
            ExoPlayer.Builder(requireContext()).build()
        }

        // Setting up the ViewPager2
        binding.apply {
            vpReels.adapter = adapter
            
            // Set up page change callback to control video playback
            pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    playVideoAtPosition(position)
                    pauseVideoAtPosition(currentPosition)
                    currentPosition = position
                }
            }
            vpReels.registerOnPageChangeCallback(pageChangeCallback!!)
        }

        setupObservers()

        if (reelsViewModel.reels.value.isNullOrEmpty()) {
            reelsViewModel.loadReels()
        } else {
            // If data already exists, play the first video
            binding.vpReels.post {
                playVideoAtPosition(0)
            }
        }
    }

    private fun setupObservers() {
        reelsViewModel.reels.observe(viewLifecycleOwner) {
            it?.let {
                adapter.reels.clear()
                adapter.reels.addAll(it)
                adapter.notifyDataSetChanged()
                binding.vpReels.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
                
                // Play the first video when data is loaded
                if (it.isNotEmpty()) {
                    binding.vpReels.post {
                        playVideoAtPosition(0)
                    }
                }
            }
        }

        reelsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if(isLoading) {
                binding.pbReels.visibility = View.VISIBLE
                binding.vpReels.visibility = View.GONE
                binding.tvErrorReels.visibility = View.GONE
            } else {
                binding.pbReels.visibility = View.GONE
                binding.vpReels.visibility = View.VISIBLE
                binding.tvErrorReels.visibility = View.GONE
            }
        }

        reelsViewModel.error.observe(viewLifecycleOwner) { error ->
            if(!error.isNullOrEmpty()) {
                binding.pbReels.visibility = View.GONE
                binding.vpReels.visibility = View.GONE
                binding.tvErrorReels.visibility = View.VISIBLE
            } else {
                binding.tvErrorReels.visibility = View.GONE
            }
        }
    }

    private fun playVideoAtPosition(position: Int) {
        if (position < 0 || position >= adapter.reels.size) return
        
        // Get the ViewHolder for the current position
        val recyclerView = binding.vpReels.getChildAt(0) as? RecyclerView
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? ReelsAdapter.ReelViewHolder
        
        viewHolder?.let { holder ->
            holder.player?.let { player ->
                if (player.playbackState != Player.STATE_READY) {
                    // Wait for player to be ready
                    player.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_READY && !player.isPlaying) {
                                player.play()
                                player.removeListener(this)
                            }
                        }
                    })
                } else {
                    if (!player.isPlaying) {
                        player.play()
                    }
                }
            }
        }
    }

    private fun pauseVideoAtPosition(position: Int) {
        if (position < 0 || position >= adapter.reels.size) return
        
        // Get the ViewHolder for the position
        val recyclerView = binding.vpReels.getChildAt(0) as? RecyclerView
        val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position) as? ReelsAdapter.ReelViewHolder
        
        viewHolder?.player?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume video playback for current position
        playVideoAtPosition(currentPosition)
    }

    override fun onPause() {
        super.onPause()
        // Pause all videos when fragment is paused
        pauseVideoAtPosition(currentPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister page change callback
        pageChangeCallback?.let {
            binding.vpReels.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback = null
        
        // Release all players
        val recyclerView = binding.vpReels.getChildAt(0) as? RecyclerView
        for (i in 0 until adapter.itemCount) {
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(i) as? ReelsAdapter.ReelViewHolder
            viewHolder?.player?.release()
            viewHolder?.reelVideoPlayer?.player = null
        }
        
        _binding = null
    }
}