package com.supercom.service_locator

import com.supercom.data.source.api.CoronaVirusApi
import com.supercom.utils.Constants.Api.BASE_URL
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideRetrofit() }
    single { provideApi(get()) }
}

private fun provideRetrofit() =
    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

private fun provideApi(retrofit: Retrofit): CoronaVirusApi =
    retrofit.create(CoronaVirusApi::class.java)