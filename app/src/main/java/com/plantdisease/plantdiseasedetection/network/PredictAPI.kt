package com.plantdisease.plantdiseasedetection.network

import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PredictAPI {

    @Multipart
    @POST("/predict")
    fun uploadPhoto(
        @Part profilePhoto: MultipartBody.Part,
        @Part("plant_name") plant: RequestBody
    ): Single<UploadResponse>
}
