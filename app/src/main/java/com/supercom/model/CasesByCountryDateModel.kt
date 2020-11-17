package com.supercom.model

class CasesByCountryDateModel : ArrayList<CasesByCountryDateModel.DeathsByCountryDateModelItem>(){
    data class DeathsByCountryDateModelItem(
        val Cases: Int,
        val City: String,
        val CityCode: String,
        val Country: String,
        val CountryCode: String,
        val Date: String,
        val Lat: String,
        val Lon: String,
        val Province: String,
        val Status: String
    )
}