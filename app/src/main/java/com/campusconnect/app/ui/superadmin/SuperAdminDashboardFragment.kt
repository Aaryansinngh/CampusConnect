package com.campusconnect.app.ui.superadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.campusconnect.app.adapter.AdminUserAdapter
import com.campusconnect.app.databinding.FragmentSuperAdminDashboardBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.SuperAdminViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class SuperAdminDashboardFragment : Fragment() {
    private var _binding: FragmentSuperAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val superAdminViewModel: SuperAdminViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var userAdapter: AdminUserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSuperAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAdapter = AdminUserAdapter { user, newRole ->
            superAdminViewModel.updateUserRole(user.uid, newRole)
            Toast.makeText(requireContext(), "Updated ${user.name}'s role to $newRole", Toast.LENGTH_SHORT).show()
        }
        binding.rvUsers.adapter = userAdapter

        superAdminViewModel.usersState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressLoading.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    userAdapter.submitList(result.data ?: emptyList())
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
