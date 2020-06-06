package com.plantdisease.plantdiseasedetection

import android.app.Application
import com.plantdisease.plantdiseasedetection.di.moduleList
import org.koin.android.ext.android.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, moduleList)
    }
}