package com.campusconnect.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.adapter.PostAdapter
import com.campusconnect.app.databinding.FragmentSavedPostsBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.ProfileViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class SavedPostsFragment : Fragment() {
    private var _binding: FragmentSavedPostsBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSavedPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter(
            onLikeClicked = {},
            onCommentClicked = { post ->
                val bundle = Bundle().apply { putString("postId", post.postId) }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            },
            onSaveClicked = { post -> profileViewModel.unsavePost(post.postId) },
            onPostClicked = { post ->
                val bundle = Bundle().apply { putString("postId", post.postId) }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            }
        )
        binding.rvSavedPosts.adapter = postAdapter

        profileViewModel.loadSavedPosts()
        profileViewModel.savedPostsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressLoading.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    postAdapter.submitList(result.data ?: emptyList())
                }
                is Resource.Error -> binding.progressLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
