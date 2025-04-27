package com.project.travelbuddy.api

import com.project.travelbuddy.model.Country
import retrofit2.http.GET

interface CountryApi {
    @GET("v3.1/all")
    suspend fun getAllCountries(): List<Country>
}