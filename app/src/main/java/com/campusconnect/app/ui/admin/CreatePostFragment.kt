package com.campusconnect.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.data.model.Post
import com.campusconnect.app.databinding.FragmentCreatePostBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.AdminViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPublishPost.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            val imageUrl = binding.etImageUrl.text.toString().trim()
            val type = if (binding.rbTypeAnnouncement.isChecked) Post.TYPE_ANNOUNCEMENT else Post.TYPE_POST

            adminViewModel.createPost(
                title = title,
                content = content,
                imageUrl = imageUrl,
                type = type,
                clubId = "club_coding",
                clubName = "MUJ Coding Club",
                clubLogoUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=150"
            )
        }

        adminViewModel.createPostResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.btnPublishPost.isEnabled = false
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Post published successfully!", Toast.LENGTH_SHORT).show()
                    adminViewModel.clearResults()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.btnPublishPost.isEnabled = true
                    Toast.makeText(requireContext(), result.message ?: "Failed to publish post", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
