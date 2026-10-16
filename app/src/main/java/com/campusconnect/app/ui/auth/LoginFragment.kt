package com.campusconnect.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.data.model.User
import com.campusconnect.app.databinding.FragmentLoginBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.AuthViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            authViewModel.login(email, password)
        }

        binding.tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Password reset link sent to email.", Toast.LENGTH_SHORT).show()
        }

        binding.btnDemoStudent.setOnClickListener {
            authViewModel.switchDemoRole(User.ROLE_STUDENT)
        }

        binding.btnDemoAdmin.setOnClickListener {
            authViewModel.switchDemoRole(User.ROLE_CLUB_ADMIN)
        }

        binding.btnDemoSuper.setOnClickListener {
            authViewModel.switchDemoRole(User.ROLE_SUPER_ADMIN)
        }

        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.tvError.visibility = View.GONE
                    binding.btnLogin.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnLogin.isEnabled = true
                    findNavController().navigate(R.id.action_login_to_home)
                }
                is Resource.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.tvError.text = result.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
