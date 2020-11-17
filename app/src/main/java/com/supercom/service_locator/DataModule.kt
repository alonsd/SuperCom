package com.supercom.service_locator

import com.supercom.data.source.RemoteDataSource
import org.koin.dsl.module

val dataModule = module {
    single { RemoteDataSource(get()) }
}


