package com.campusconnect.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.databinding.FragmentCreateEventBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.AdminViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class CreateEventFragment : Fragment() {
    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPublishEvent.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val startTime = binding.etStartTime.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val deadline = binding.etDeadline.text.toString().trim()
            val capacityStr = binding.etCapacity.text.toString().trim()
            val capacity = capacityStr.toIntOrNull() ?: 100
            val imageUrl = binding.etImageUrl.text.toString().trim()

            adminViewModel.createEvent(
                title = title,
                description = description,
                date = date,
                startTime = startTime,
                endTime = "8:00 PM",
                location = location,
                deadline = deadline,
                capacity = capacity,
                imageUrl = imageUrl,
                clubId = "club_coding",
                clubName = "MUJ Coding Club"
            )
        }

        adminViewModel.createEventResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.btnPublishEvent.isEnabled = false
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Event published successfully!", Toast.LENGTH_SHORT).show()
                    adminViewModel.clearResults()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.btnPublishEvent.isEnabled = true
                    Toast.makeText(requireContext(), result.message ?: "Failed to publish event", Toast.LENGTH_SHORT).show()
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
