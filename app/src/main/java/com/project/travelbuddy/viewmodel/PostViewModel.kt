package com.project.travelbuddy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.project.travelbuddy.model.TravelPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _travelPosts = MutableLiveData<List<TravelPost>>()
    val travelPosts: LiveData<List<TravelPost>> get() = _travelPosts

    private val db = FirebaseFirestore.getInstance()

    fun fetchTravelPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("posts").get()
                .addOnSuccessListener { snapshot ->
                    val posts = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TravelPost::class.java)
                    }
                    _travelPosts.postValue(posts)
                }
                .addOnFailureListener {
                    _travelPosts.postValue(emptyList())
                }
        }
    }
}
