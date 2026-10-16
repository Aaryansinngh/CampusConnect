package com.campusconnect.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.campusconnect.app.R
import com.campusconnect.app.adapter.CategoryAdapter
import com.campusconnect.app.adapter.EventAdapter
import com.campusconnect.app.adapter.PostAdapter
import com.campusconnect.app.databinding.FragmentHomeBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.HomeViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var postAdapter: PostAdapter
    private lateinit var eventAdapter: EventAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val categoryList = listOf("All", "Technical", "Cultural", "Sports", "Entrepreneurship", "Arts", "Music", "Dance", "Literary")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.exploreClubsFragment)
        }

        binding.btnNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationsFragment)
        }

        binding.btnViewAllEvents.setOnClickListener {
            findNavController().navigate(R.id.eventsFragment)
        }

        binding.btnExploreClubs.setOnClickListener {
            findNavController().navigate(R.id.exploreClubsFragment)
        }
    }

    private fun setupRecyclerViews() {
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            homeViewModel.selectCategory(category)
        }
        binding.rvCategories.adapter = categoryAdapter

        eventAdapter = EventAdapter { event ->
            val bundle = Bundle().apply { putString("eventId", event.eventId) }
            findNavController().navigate(R.id.eventDetailFragment, bundle)
        }
        binding.rvUpcomingEvents.adapter = eventAdapter

        postAdapter = PostAdapter(
            onLikeClicked = { post -> homeViewModel.toggleLike(post.postId) },
            onCommentClicked = { post ->
                val bundle = Bundle().apply { putString("postId", post.postId) }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            },
            onSaveClicked = { post -> homeViewModel.toggleSave(post.postId) },
            onPostClicked = { post ->
                val bundle = Bundle().apply { putString("postId", post.postId) }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            }
        )
        binding.rvFeedPosts.adapter = postAdapter
    }

    private fun observeViewModel() {
        homeViewModel.postsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                    binding.layoutEmptyFeed.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    val posts = result.data ?: emptyList()
                    if (posts.isEmpty()) {
                        binding.layoutEmptyFeed.visibility = View.VISIBLE
                        binding.rvFeedPosts.visibility = View.GONE
                    } else {
                        binding.layoutEmptyFeed.visibility = View.GONE
                        binding.rvFeedPosts.visibility = View.VISIBLE
                        postAdapter.submitList(posts)
                    }
                }
                is Resource.Error -> {
                    binding.progressLoading.visibility = View.GONE
                }
            }
        }

        homeViewModel.eventsState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                eventAdapter.submitList(result.data ?: emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
