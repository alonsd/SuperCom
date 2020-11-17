package com.supercom.data.source

import com.supercom.data.source.api.CoronaVirusApi
import com.supercom.model.CasesByCountryDateModel
import com.supercom.utils.network.Resource

class RemoteDataSource(private val coronaVirusApi: CoronaVirusApi) {

    suspend fun getCovidCasesByDateFromCountry(country: String, from: String, to: String): Resource<CasesByCountryDateModel> {
        return try {
            Resource.Success(coronaVirusApi.getCovidDeathsByDeathFromCountry(country, from, to))
        } catch (exception: Exception) {
            Resource.Exception(exception)
        }
    }

}