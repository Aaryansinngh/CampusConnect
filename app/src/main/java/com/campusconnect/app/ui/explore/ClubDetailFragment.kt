package com.campusconnect.app.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.adapter.PostAdapter
import com.campusconnect.app.databinding.FragmentClubDetailBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.ClubViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class ClubDetailFragment : Fragment() {
    private var _binding: FragmentClubDetailBinding? = null
    private val binding get() = _binding!!

    private val clubViewModel: ClubViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var postAdapter: PostAdapter
    private var clubId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClubDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clubId = arguments?.getString("clubId") ?: ""
        if (clubId.isNotBlank()) {
            clubViewModel.loadClubDetail(clubId)
        }

        postAdapter = PostAdapter(
            onLikeClicked = {},
            onCommentClicked = { post ->
                val bundle = Bundle().apply { putString("postId", post.postId) }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            },
            onSaveClicked = {},
            onPostClicked = { post ->
                val bundle = Bundle().apply { putString("postId", post.postId) }
                findNavController().navigate(R.id.postDetailFragment, bundle)
            }
        )
        binding.rvClubPosts.adapter = postAdapter

        clubViewModel.selectedClubState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val club = result.data ?: return@observe
                binding.tvClubName.text = club.name
                binding.tvClubCategory.text = club.category
                binding.tvFollowerCount.text = "${club.followerCount} Followers"
                binding.tvAbout.text = club.description

                if (club.coverImageUrl.isNotBlank()) {
                    Glide.with(requireContext()).load(club.coverImageUrl).into(binding.imgCover)
                }
                if (club.logoUrl.isNotBlank()) {
                    Glide.with(requireContext()).load(club.logoUrl).into(binding.imgClubLogo)
                }

                if (club.followedByCurrentUser) {
                    binding.btnFollow.text = getString(R.string.following)
                    binding.btnFollow.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_bg))
                    binding.btnFollow.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                } else {
                    binding.btnFollow.text = getString(R.string.follow)
                    binding.btnFollow.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    binding.btnFollow.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_primary))
                }

                binding.btnFollow.setOnClickListener {
                    clubViewModel.toggleFollow(club.clubId)
                }
            }
        }

        clubViewModel.clubPostsState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                postAdapter.submitList(result.data ?: emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
