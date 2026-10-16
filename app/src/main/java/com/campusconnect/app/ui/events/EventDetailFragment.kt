package com.campusconnect.app.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.campusconnect.app.R
import com.campusconnect.app.databinding.FragmentEventDetailBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.EventViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class EventDetailFragment : Fragment() {
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels { ViewModelFactory(requireContext()) }
    private var eventId: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventId = arguments?.getString("eventId") ?: ""
        if (eventId.isNotBlank()) {
            eventViewModel.loadEventDetail(eventId)
        }

        eventViewModel.selectedEventState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val event = result.data ?: return@observe
                binding.tvEventClub.text = event.clubName
                binding.tvEventTitle.text = event.title
                binding.tvEventDatetime.text = "${event.date} • ${event.startTime} - ${event.endTime}"
                binding.tvEventLocation.text = event.location
                binding.tvEventDeadline.text = "Registration Deadline: ${event.registrationDeadline}"
                binding.tvEventDescription.text = event.description

                binding.tvCapacityStats.text = "${event.registrationCount} / ${event.capacity}"
                val progress = if (event.capacity > 0) (event.registrationCount * 100) / event.capacity else 0
                binding.pbCapacity.progress = progress.coerceIn(0, 100)

                if (event.imageUrl.isNotBlank()) {
                    Glide.with(requireContext()).load(event.imageUrl).into(binding.imgEventPoster)
                }

                // Button State Logic
                if (event.isRegisteredByCurrentUser) {
                    binding.btnRegister.text = getString(R.string.registered)
                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_registered_text))
                } else if (event.registrationCount >= event.capacity) {
                    binding.btnRegister.text = getString(R.string.event_full)
                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_rose))
                } else {
                    binding.btnRegister.text = getString(R.string.register)
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    binding.btnRegister.setOnClickListener {
                        eventViewModel.registerForEvent(event.eventId)
                    }
                }
            }
        }

        eventViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.btnRegister.isEnabled = false
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Your registration for event is confirmed! 🎉", Toast.LENGTH_LONG).show()
                    eventViewModel.clearRegistrationResult()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), result.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                    binding.btnRegister.isEnabled = true
                    eventViewModel.clearRegistrationResult()
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
