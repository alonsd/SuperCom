package com.supercom.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.supercom.data.repository.MainRepository
import com.supercom.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getCovidDeathsByCountryAndDate(country: String, from: String, to: String) = liveData {

//        viewModelScope.launch(Dispatchers.IO) {
            emit(Resource.Loading<Nothing>())
            emit(mainRepository.getCovidDeathsByDeathFromCountry(country, from, to))
//        }
    }
}