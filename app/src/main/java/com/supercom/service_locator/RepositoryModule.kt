package com.supercom.service_locator

import com.supercom.data.repository.MainRepository
import org.koin.dsl.module


val repositoryModule = module {

    single { MainRepository(get()) }
}