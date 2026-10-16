package com.campusconnect.app.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.adapter.CategoryAdapter
import com.campusconnect.app.adapter.EventAdapter
import com.campusconnect.app.databinding.FragmentEventsBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.EventViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var eventAdapter: EventAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val categories = listOf("All", "Technical", "Cultural", "Sports", "Music", "Other")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryAdapter(categories) { category ->
            eventViewModel.loadEvents(category = category)
        }
        binding.rvCategories.adapter = categoryAdapter

        eventAdapter = EventAdapter { event ->
            val bundle = Bundle().apply { putString("eventId", event.eventId) }
            findNavController().navigate(R.id.eventDetailFragment, bundle)
        }
        binding.rvEvents.adapter = eventAdapter

        binding.btnMyEvents.setOnClickListener {
            findNavController().navigate(R.id.myEventsFragment)
        }

        eventViewModel.eventsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressLoading.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    eventAdapter.submitList(result.data ?: emptyList())
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
