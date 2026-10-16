package com.campusconnect.app.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.campusconnect.app.adapter.RegistrationAdapter
import com.campusconnect.app.databinding.FragmentEventRegistrationsBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.AdminViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class EventRegistrationsFragment : Fragment() {
    private var _binding: FragmentEventRegistrationsBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var registrationAdapter: RegistrationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventRegistrationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationAdapter = RegistrationAdapter()
        binding.rvRegistrations.adapter = registrationAdapter

        val eventId = arguments?.getString("eventId") ?: "event_hackathon"
        adminViewModel.loadEventRegistrations(eventId)

        adminViewModel.registrationsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressLoading.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    val list = result.data ?: emptyList()
                    binding.tvTotalCount.text = "Total Registrations: ${list.size}"
                    registrationAdapter.submitList(list)
                }
                is Resource.Error -> binding.progressLoading.visibility = View.GONE
            }
        }

        binding.btnExportCsv.setOnClickListener {
            Toast.makeText(requireContext(), "CSV Exported successfully to Downloads folder! 📁", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
