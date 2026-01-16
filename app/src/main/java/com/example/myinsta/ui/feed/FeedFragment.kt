package com.example.myinsta.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myinsta.R
import com.example.myinsta.api.RetrofitInstance
import com.example.myinsta.data.AppDatabase
import com.example.myinsta.data.PostRepository
import com.example.myinsta.databinding.FragmentFeedBinding
import kotlinx.coroutines.launch

class FeedFragment: Fragment() {

    private var _binding: FragmentFeedBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: FeedAdapter
    private lateinit var repository: PostRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repository
        val apiService = RetrofitInstance.api
        val postDao = AppDatabase.getDatabase(requireContext()).postDao()
        repository = PostRepository(apiService, postDao)

        // Create adapter with empty list - will be updated when data loads
        adapter = FeedAdapter(mutableListOf(), repository, lifecycleScope)
        
        // Setup RecyclerView outside coroutine
        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeed.adapter = adapter

        // Load data in coroutine
        loadFeed()
    }

    private fun loadFeed() {
        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE
        binding.rvFeed.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val feed = repository.getPosts()
                
                // Update adapter with new data
                adapter.posts.clear()
                adapter.posts.addAll(feed)
                adapter.notifyItemRangeInserted(0, feed.size)
                
                // Show RecyclerView, hide loading
                binding.progressBar.visibility = View.GONE
                binding.rvFeed.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
                
            } catch (e: Exception) {
                // Show error message, hide loading and RecyclerView
                binding.progressBar.visibility = View.GONE
                binding.rvFeed.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}