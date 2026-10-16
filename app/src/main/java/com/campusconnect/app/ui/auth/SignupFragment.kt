package com.campusconnect.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.campusconnect.app.R
import com.campusconnect.app.databinding.FragmentSignupBinding
import com.campusconnect.app.utils.Resource
import com.campusconnect.app.viewmodel.AuthViewModel
import com.campusconnect.app.viewmodel.ViewModelFactory

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignup.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val studentId = binding.etStudentId.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (password != confirmPassword) {
                binding.tvError.text = "Passwords do not match."
                binding.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            authViewModel.signup(name, email, password, studentId)
        }

        binding.tvLoginLink.setOnClickListener {
            findNavController().navigateUp()
        }

        authViewModel.authState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.tvError.visibility = View.GONE
                    binding.btnSignup.isEnabled = false
                }
                is Resource.Success -> {
                    binding.btnSignup.isEnabled = true
                    findNavController().navigate(R.id.action_signup_to_home)
                }
                is Resource.Error -> {
                    binding.btnSignup.isEnabled = true
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
