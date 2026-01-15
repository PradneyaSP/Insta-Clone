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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = RetrofitInstance.api
        val postDao = AppDatabase.getDatabase(requireContext()).postDao()
        val repository = PostRepository(apiService, postDao)

        lifecycleScope.launch {
            val feed = repository.getPosts()
            val adapter = FeedAdapter(feed, repository)
            binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
            binding.rvFeed.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}