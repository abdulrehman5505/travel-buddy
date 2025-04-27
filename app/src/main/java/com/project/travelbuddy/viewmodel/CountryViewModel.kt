package com.project.travelbuddy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.travelbuddy.db.CountryEntity
import com.project.travelbuddy.repo.CountryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CountryViewModel(private val repository: CountryRepository) : ViewModel() {

    private val _countries = MutableLiveData<List<CountryEntity>>()
    val countries: LiveData<List<CountryEntity>> get() = _countries

    fun fetchCountriesData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getCountriesData()
            _countries.postValue(data)
        }
    }
}