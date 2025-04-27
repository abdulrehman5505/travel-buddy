package com.project.travelbuddy.repo

import com.project.travelbuddy.api.ApiService
import com.project.travelbuddy.db.CountryDao
import com.project.travelbuddy.db.CountryEntity

class CountryRepository(private val countryDao: CountryDao) {

    suspend fun getCountriesData(): List<CountryEntity> {
        // Check if we have cached country data
        var countries = countryDao.getAllCountries()

        if (countries.isEmpty()) {
            // Fetch from API if not available offline
            val countriesFromApi = ApiService.retrofitService.getAllCountries()

            countries = countriesFromApi.map { country ->
                CountryEntity(
                    name = country.name.common,
                    capital = country.capital?.firstOrNull() ?: "N/A",
                    region = country.region,
                    population = country.population,
                    flag = country.flags.png
                )
            }

            // Save to Room database
            countryDao.insertAll(countries)
        }

        return countries
    }
}
