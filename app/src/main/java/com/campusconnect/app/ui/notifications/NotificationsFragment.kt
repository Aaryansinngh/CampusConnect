package com.campusconnect.app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.campusconnect.app.adapter.NotificationAdapter
import com.campusconnect.app.databinding.FragmentNotificationsBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.NotificationViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val notificationViewModel: NotificationViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationAdapter = NotificationAdapter { notification ->
            notificationViewModel.markAsRead(notification.notificationId)
        }
        binding.rvNotifications.adapter = notificationAdapter

        notificationViewModel.notificationsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressLoading.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    notificationAdapter.submitList(result.data ?: emptyList())
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
