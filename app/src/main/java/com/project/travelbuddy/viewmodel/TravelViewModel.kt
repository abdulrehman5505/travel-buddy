package com.project.travelbuddy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.project.travelbuddy.model.TravelPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TravelViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val _travelPosts = MutableLiveData<List<TravelPost>>()
    val travelPosts: LiveData<List<TravelPost>> = _travelPosts

    init {
        fetchTravelPosts()
    }

    private fun fetchTravelPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val posts = snapshot.toObjects(TravelPost::class.java)
                        _travelPosts.value = posts
                    }
                }
        }
    }
}
