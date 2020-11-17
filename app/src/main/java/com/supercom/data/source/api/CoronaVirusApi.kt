package com.supercom.data.source.api

import com.supercom.model.CasesByCountryDateModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoronaVirusApi {

    @GET("country/{country}/status/deaths?")
    suspend fun getCovidDeathsByDeathFromCountry(
        @Path("country") country: String,
        @Query("from", encoded = true) from : String,
        @Query("to", encoded = true) to : String
    ): CasesByCountryDateModel
}