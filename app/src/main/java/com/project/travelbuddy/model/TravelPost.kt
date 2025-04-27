package com.project.travelbuddy.model

data class TravelPost(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val location: String = "",
    val authorId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

