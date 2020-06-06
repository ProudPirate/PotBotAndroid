package com.plantdisease.plantdiseasedetection.di

import com.plantdisease.plantdiseasedetection.network.UploadRepository
import com.plantdisease.plantdiseasedetection.ui.main.viewmodel.MainViewModel
import org.koin.dsl.module.module

private val appModule = module {
    single { MainViewModel(get()) }
    single { UploadRepository(get()) }
}

val moduleList = listOf(
    appModule,
    networkModule
)