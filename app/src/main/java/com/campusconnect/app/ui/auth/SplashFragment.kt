package com.campusconnect.app.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.databinding.FragmentSplashBinding
import com.campusconnect.app.viewmodel.AuthViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
                    if (user != null) {
                        findNavController().navigate(R.id.action_splash_to_home)
                    } else {
                        findNavController().navigate(R.id.action_splash_to_login)
                    }
                }
            }
        }, 1200)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
