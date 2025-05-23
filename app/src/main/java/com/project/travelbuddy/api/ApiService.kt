package com.project.travelbuddy.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
 
object ApiService {
    private const val BASE_URL = "https://restcountries.com/"
 
    val retrofitService: CountryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountryApi::class.java)
    }
} 