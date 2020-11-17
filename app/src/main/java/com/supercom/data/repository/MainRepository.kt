package com.supercom.data.repository

import com.supercom.data.source.RemoteDataSource
import com.supercom.model.CountryCasesModel
import com.supercom.utils.network.Resource

class MainRepository(private val remoteDataSource: RemoteDataSource) {

    suspend fun getCovidDeathsByDeathFromCountry(country: String, from: String, to: String): Resource<*> {

        val covidDeathsByDeathFromCountry = remoteDataSource.getCovidCasesByDateFromCountry(country, from, to)
        if (covidDeathsByDeathFromCountry is Resource.Exception) {
            return covidDeathsByDeathFromCountry
        }

        val result = CountryCasesModel(country, covidDeathsByDeathFromCountry.data!!.last().Cases)
        return Resource.Success(result)
    }

}

