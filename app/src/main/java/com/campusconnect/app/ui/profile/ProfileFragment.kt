package com.campusconnect.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.data.model.User
import com.campusconnect.app.databinding.FragmentProfileBinding
import com.campusconnect.app.viewmodel.AuthViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email
                binding.tvStudentId.text = "Student ID: ${user.studentId}"
                binding.tvRoleBadge.text = user.role

                if (user.profileImageUrl.isNotBlank()) {
                    Glide.with(requireContext()).load(user.profileImageUrl).into(binding.imgProfileAvatar)
                }

                if (user.role == User.ROLE_CLUB_ADMIN || user.role == User.ROLE_SUPER_ADMIN) {
                    binding.btnMenuAdmin.visibility = View.VISIBLE
                } else {
                    binding.btnMenuAdmin.visibility = View.GONE
                }

                if (user.role == User.ROLE_SUPER_ADMIN) {
                    binding.btnMenuSuperAdmin.visibility = View.VISIBLE
                } else {
                    binding.btnMenuSuperAdmin.visibility = View.GONE
                }
            }
        }

        binding.btnMenuMyEvents.setOnClickListener {
            findNavController().navigate(R.id.myEventsFragment)
        }

        binding.btnMenuSavedPosts.setOnClickListener {
            findNavController().navigate(R.id.savedPostsFragment)
        }

        binding.btnMenuAdmin.setOnClickListener {
            findNavController().navigate(R.id.adminDashboardFragment)
        }

        binding.btnMenuSuperAdmin.setOnClickListener {
            findNavController().navigate(R.id.superAdminDashboardFragment)
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.action_splash_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
