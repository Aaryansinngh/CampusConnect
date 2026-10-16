package com.campusconnect.app.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.adapter.EventAdapter
import com.campusconnect.app.databinding.FragmentMyEventsBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.EventViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class MyEventsFragment : Fragment() {
    private var _binding: FragmentMyEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter { event ->
            val bundle = Bundle().apply { putString("eventId", event.eventId) }
            findNavController().navigate(R.id.eventDetailFragment, bundle)
        }
        binding.rvMyEvents.adapter = eventAdapter

        eventViewModel.loadEvents()
        eventViewModel.eventsState.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success) {
                val registeredOnly = (result.data ?: emptyList()).filter { it.isRegisteredByCurrentUser }
                eventAdapter.submitList(registeredOnly)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
