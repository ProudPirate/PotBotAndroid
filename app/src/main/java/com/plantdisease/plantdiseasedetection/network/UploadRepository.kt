package com.plantdisease.plantdiseasedetection.network

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UploadRepository(
    private val predictAPI: PredictAPI
) {

    fun uploadPhoto(image: String, plant: String = "plant_test"): Single<UploadResponse> {
        return predictAPI.uploadPhoto(
            prepareFilePart("file", image),
            createStringRequestBody(plant)
        ).subscribeOn(Schedulers.io())
    }
}