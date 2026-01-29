package com.example.myinsta.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myinsta.data.PostRepository
import com.example.myinsta.databinding.FragmentFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment: Fragment() {

    private var _binding: FragmentFeedBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Field Injection
    @Inject
    lateinit var repository: PostRepository

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var adapter: FeedAdapter

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

        // Create adapter with empty list - will be updated when data loads
        adapter = FeedAdapter(mutableListOf(), repository, lifecycleScope, viewModel)
        
        // Setup RecyclerView
        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeed.adapter = adapter

        // Observe the Data
        setupObservers()

        if (viewModel.feed.value.isNullOrEmpty()) {
            viewModel.loadFeed()
        }
    }

    private fun setupObservers(){
        viewModel.feed.observe(viewLifecycleOwner) { feed ->
            feed?.let {
                adapter.posts.clear()
                adapter.posts.addAll(it)
                adapter.notifyDataSetChanged()
                binding.rvFeed.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if(isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvFeed.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvFeed.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if(!error.isNullOrEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.rvFeed.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}