package com.project.travelbuddy.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.travelbuddy.R
import com.project.travelbuddy.adapter.TravelPostAdapter
import com.project.travelbuddy.databinding.FragmentProfileBinding
import com.project.travelbuddy.model.TravelPost
import com.project.travelbuddy.model.UserProfile
import com.project.travelbuddy.util.Constant.showToast
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: TravelPostAdapter
    private var newImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("users")
        auth = FirebaseAuth.getInstance()

        loadUserProfile()
        loadUserPosts()

        binding.btnChangeProfilePicture.setOnClickListener { pickNewImage() }
        binding.btnUpdateProfile.setOnClickListener { updateProfile() }
        binding.btnLogout.setOnClickListener { logoutUser() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        })
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserProfile::class.java)
                    user?.let {
                        binding.etDisplayName.setText(it.displayName)
                        if (it.profileImageUrl.isNotEmpty()){
                            Picasso.get().load(it.profileImageUrl).into(binding.ivProfilePicture)
                        }
                    }
                }
            }
    }

    private fun loadUserPosts() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("posts")
            .whereEqualTo("authorId", userId)
            .get().addOnSuccessListener { querySnapshot ->

                val posts = querySnapshot.toObjects(TravelPost::class.java)

                adapter = TravelPostAdapter(posts) { postId ->
                    val bundle = bundleOf("postId" to postId)
                    findNavController().navigate(R.id.action_profileFragment_to_postDetailFragment, bundle)
                }
                binding.recyclerViewMyPosts.adapter = adapter
            }
            .addOnFailureListener { e ->
                showToast(requireContext(), "Error fetching user posts: ${e.message}")
            }
    }

    private fun pickNewImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            newImageUri = data.data
            binding.ivProfilePicture.setImageURI(newImageUri)
        }
    }

    private fun updateProfile() {
        val userId = auth.currentUser?.uid ?: return
        val updatedName = binding.etDisplayName.text.toString().trim()

        if (updatedName.isEmpty()) {
            showToast(requireContext(), "Name cannot be empty!")
            return
        }

        if (newImageUri != null) {
            uploadNewImageAndUpdateProfile(userId, updatedName)
        } else {
            updateProfileInFirestore(userId, updatedName, null)
        }
    }

    private fun uploadNewImageAndUpdateProfile(userId: String, updatedName: String) {
        val imageRef = storageRef.child("$userId.jpg")

        imageRef.putFile(newImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { newImageUrl ->
                    updateProfileInFirestore(userId, updatedName, newImageUrl.toString())
                }
            }
            .addOnFailureListener {
                showToast(requireContext(), "Image upload failed!")
            }
    }

    private fun updateProfileInFirestore(userId: String, updatedName: String, newImageUrl: String?) {
        val updateData = mutableMapOf<String, Any>("displayName" to updatedName)
        newImageUrl?.let { updateData["profileImageUrl"] = it }

        firestore.collection("users").document(userId)
            .update(updateData)
            .addOnSuccessListener {
                showToast(requireContext(), "Profile updated successfully!")
            }
            .addOnFailureListener {
                showToast(requireContext(), "Failed to update profile!")
            }
    }

    private fun logoutUser() {
        auth.signOut()
        findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
