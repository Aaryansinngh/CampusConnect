package com.campusconnect.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {
    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActionCreatePost.setOnClickListener {
            findNavController().navigate(R.id.createPostFragment)
        }

        binding.btnActionCreateEvent.setOnClickListener {
            findNavController().navigate(R.id.createEventFragment)
        }

        binding.btnActionViewRegistrations.setOnClickListener {
            findNavController().navigate(R.id.eventRegistrationsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
