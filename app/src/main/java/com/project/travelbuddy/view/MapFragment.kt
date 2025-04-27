package com.project.travelbuddy.view

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.travelbuddy.R
import com.project.travelbuddy.viewmodel.PostViewModel

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var postViewModel: PostViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // Get the map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        postViewModel.fetchTravelPosts()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homeFragment)
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Observe travel posts and add markers
        postViewModel.travelPosts.observe(viewLifecycleOwner) { posts ->
            posts.forEach { post ->
                val location = LatLng(post.latitude, post.longitude)
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(post.title)
                        .snippet(post.description)
                )
                marker?.tag = post.id
            }

            // Move the camera to the first post location (if available)
            if (posts.isNotEmpty()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(posts[0].latitude, posts[0].longitude), 5f))
            }
            googleMap.setOnMarkerClickListener { marker ->
                val postId = marker.tag as? String
                if (postId != null) {
                    val bundle = Bundle()
                    bundle.putString("postId", postId)
                    findNavController().navigate(R.id.postDetailFragment, bundle)
                }
                true
            }
        }
    }
}
