package com.campusconnect.app.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.adapter.CommentAdapter
import com.campusconnect.app.databinding.FragmentPostDetailBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.utils.SessionManager
import com.campusconnect.app.viewmodel.PostViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var commentAdapter: CommentAdapter
    private var postId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = arguments?.getString("postId") ?: ""
        val currentUserId = SessionManager(requireContext()).getUser()?.uid ?: ""

        commentAdapter = CommentAdapter(currentUserId) { comment ->
            postViewModel.deleteComment(comment.commentId, postId)
        }
        binding.rvComments.adapter = commentAdapter

        if (postId.isNotBlank()) {
            postViewModel.loadPostDetail(postId)
        }

        binding.btnSendComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text.isNotBlank()) {
                postViewModel.addComment(postId, text)
                binding.etComment.setText("")
            }
        }

        postViewModel.postDetailState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val post = result.data ?: return@observe
                binding.tvClubName.text = post.clubName
                binding.tvPostTitle.text = post.title
                binding.tvPostContent.text = post.content

                val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                binding.tvPostTime.text = sdf.format(Date(post.createdAt))

                if (post.imageUrl.isNotBlank()) {
                    binding.imgPostMedia.visibility = View.VISIBLE
                    Glide.with(requireContext()).load(post.imageUrl).into(binding.imgPostMedia)
                }
                if (post.clubLogoUrl.isNotBlank()) {
                    Glide.with(requireContext()).load(post.clubLogoUrl).into(binding.imgClubLogo)
                }
            }
        }

        postViewModel.commentsState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                commentAdapter.submitList(result.data ?: emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
