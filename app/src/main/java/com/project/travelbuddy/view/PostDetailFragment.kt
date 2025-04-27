package com.project.travelbuddy.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.travelbuddy.R
import com.project.travelbuddy.databinding.FragmentPostDetailBinding
import com.project.travelbuddy.model.TravelPost
import com.project.travelbuddy.util.Constant.showToast
import com.squareup.picasso.Picasso

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private lateinit var binding: FragmentPostDetailBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var postId: String? = null
    private var postOwnerId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPostDetailBinding.bind(view)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get Post ID from SafeArgs
        arguments?.let {
            postId = it.getString("postId")
        }

        if (postId != null) {
            loadPostDetails()
        }

        binding.btnEdit.setOnClickListener { editPost() }
        binding.btnDelete.setOnClickListener { deletePost() }
    }

    private fun loadPostDetails() {
        firestore.collection("posts").document(postId!!)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val post = document.toObject(TravelPost::class.java)
                    post?.let {
                        binding.tvTitle.text = it.title
                        binding.tvDescription.text = it.description
                        binding.tvLocation.text = it.location
                        Picasso.get().load(it.imageUrl).into(binding.ivPostImage)

                        postOwnerId = it.authorId
                        if (auth.currentUser?.uid == postOwnerId) {
                            binding.layoutButtons.visibility = View.VISIBLE
                        }
                    }
                }
            }
    }

    private fun editPost() {
        val bundle = bundleOf("postId" to postId)
        findNavController().navigate(R.id.action_postDetailFragment_to_editPostFragment, bundle)
    }

    private fun deletePost() {
        firestore.collection("posts").document(postId!!)
            .delete()
            .addOnSuccessListener {
                showToast(requireContext(), "Post deleted!")
                findNavController().navigateUp()
            }
            .addOnFailureListener {
                showToast(requireContext(), "Error deleting post!")
            }
    }
}
