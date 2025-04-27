package com.project.travelbuddy.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.travelbuddy.R
import com.project.travelbuddy.databinding.FragmentEditPostBinding
import com.project.travelbuddy.model.TravelPost
import com.project.travelbuddy.util.Constant.showToast
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment(R.layout.fragment_edit_post) {

    private lateinit var binding: FragmentEditPostBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private var postId: String? = null
    private var currentImageUrl: String? = null
    private var newImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditPostBinding.bind(view)
        firestore = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("posts")

        arguments?.let {
            postId = it.getString("postId")
        }

        if (postId != null) {
            loadPostDetails()
        }

        binding.btnPickNewImage.setOnClickListener { pickNewImage() }
        binding.btnUpdate.setOnClickListener { updatePost() }
    }

    private fun loadPostDetails() {
        firestore.collection("posts").document(postId!!)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val post = document.toObject(TravelPost::class.java)
                    post?.let {
                        binding.etTitle.setText(it.title)
                        binding.etDescription.setText(it.description)
                        binding.etLocation.setText(it.location)
                        currentImageUrl = it.imageUrl
                        Picasso.get().load(it.imageUrl).into(binding.ivPostImage)
                    }
                }
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
            binding.ivPostImage.setImageURI(newImageUri)
        }
    }

    private fun updatePost() {
        val updatedTitle = binding.etTitle.text.toString().trim()
        val updatedDescription = binding.etDescription.text.toString().trim()
        val updatedLocation = binding.etLocation.text.toString().trim()

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedLocation.isEmpty()) {
            showToast(requireContext(), "All fields are required!")
            return
        }

        if (newImageUri != null) {
            uploadNewImageAndUpdatePost(updatedTitle, updatedDescription, updatedLocation)
        } else {
            updatePostInFirestore(updatedTitle, updatedDescription, updatedLocation, currentImageUrl!!)
        }
    }

    private fun uploadNewImageAndUpdatePost(title: String, description: String, location: String) {
        val imageRef = storageRef.child("${System.currentTimeMillis()}.jpg")

        imageRef.putFile(newImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { newImageUrl ->
                    updatePostInFirestore(title, description, location, newImageUrl.toString())
                }
            }
            .addOnFailureListener {
                showToast(requireContext(), "Image upload failed!")
            }
    }

    private fun updatePostInFirestore(title: String, description: String, location: String, imageUrl: String) {
        val updateData = mapOf(
            "title" to title,
            "description" to description,
            "location" to location,
            "imageUrl" to imageUrl
        )

        firestore.collection("posts").document(postId!!)
            .update(updateData)
            .addOnSuccessListener {
                showToast(requireContext(), "Post updated successfully!")
                findNavController().navigateUp()
            }
            .addOnFailureListener {
                showToast(requireContext(), "Failed to update post!")
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}

