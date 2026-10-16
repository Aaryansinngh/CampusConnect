package com.campusconnect.app.ui.explore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.adapter.CategoryAdapter
import com.campusconnect.app.adapter.ClubAdapter
import com.campusconnect.app.databinding.FragmentExploreClubsBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.ClubViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class ExploreClubsFragment : Fragment() {
    private var _binding: FragmentExploreClubsBinding? = null
    private val binding get() = _binding!!

    private val clubViewModel: ClubViewModel by viewModels { ViewModelFactory(requireContext()) }
    private lateinit var clubAdapter: ClubAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val categories = listOf("All", "Technical", "Cultural", "Sports", "Entrepreneurship", "Arts", "Music", "Dance")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreClubsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryAdapter = CategoryAdapter(categories) { category ->
            clubViewModel.filterCategory(category)
        }
        binding.rvCategories.adapter = categoryAdapter

        clubAdapter = ClubAdapter(
            onFollowClicked = { club -> clubViewModel.toggleFollow(club.clubId) },
            onClubClicked = { club ->
                val bundle = Bundle().apply { putString("clubId", club.clubId) }
                findNavController().navigate(R.id.clubDetailFragment, bundle)
            }
        )
        binding.rvClubs.adapter = clubAdapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clubViewModel.searchClubs(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        clubViewModel.clubsState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> binding.progressLoading.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    clubAdapter.submitList(result.data ?: emptyList())
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
