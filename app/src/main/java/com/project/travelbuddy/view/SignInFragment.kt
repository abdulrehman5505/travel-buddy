package com.project.travelbuddy.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.project.travelbuddy.R
import com.project.travelbuddy.databinding.FragmentSignInBinding
import com.project.travelbuddy.util.Constant.showToast
import com.project.travelbuddy.util.Constant.validateData

class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var binding: FragmentSignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.loginBtn.setOnClickListener {
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            val validatedData = validateData(
                binding.tieEmail,
                binding.tiePassword,
                binding.tilEmail,
                binding.tilPassword
            )
            if (validatedData != null) {
                val (email, password) = validatedData
                signInUser(email, password)
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    showToast(requireContext(), "Authentication failed.")
                }
            }
    }

}