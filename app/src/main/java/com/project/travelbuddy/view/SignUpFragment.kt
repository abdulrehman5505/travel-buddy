package com.project.travelbuddy.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.travelbuddy.R
import com.project.travelbuddy.databinding.FragmentSignUpBinding
import com.project.travelbuddy.model.UserProfile
import com.project.travelbuddy.util.Constant.showToast
import com.project.travelbuddy.util.Constant.validateData

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignUpBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.registerBtn.setOnClickListener {
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
                signUpUser(email, password)
            }
        }

    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToFirestore(userId, email)
                    }
                } else {
                    showToast(requireContext(), "Registration failed.")
                }
            }
    }

    private fun saveUserToFirestore(userId: String, email: String) {
        val userProfile = UserProfile(userId, email.substringBefore("@"), "") // Default name, no profile pic
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                showToast(requireContext(), "User profile created!")
                findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
            }
            .addOnFailureListener {
                showToast(requireContext(), "Failed to create profile!")
            }
    }

}