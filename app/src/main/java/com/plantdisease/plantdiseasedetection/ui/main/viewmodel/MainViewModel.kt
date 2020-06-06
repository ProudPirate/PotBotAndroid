package com.plantdisease.plantdiseasedetection.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.plantdisease.plantdiseasedetection.network.UploadRepository
import com.plantdisease.plantdiseasedetection.network.UploadResponse
import com.plantdisease.plantdiseasedetection.utils.ResultState
import com.plantdisease.plantdiseasedetection.utils.SingleLiveEvent

class MainViewModel(
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private val _uploadResult = SingleLiveEvent<ResultState<UploadResponse>>()
    val uploadResult: LiveData<ResultState<UploadResponse>> by lazy { _uploadResult }

    internal var photoPath: String = ""
    internal var plant: String = ""

    fun uploadPhoto() {
        uploadRepository.uploadPhoto(photoPath, plant)
            .doOnSubscribe {
                _uploadResult.postValue(ResultState.loading())
            }
            .subscribe({
                _uploadResult.postValue(ResultState.success(it))
            }, {
                _uploadResult.postValue(ResultState.error(it.message))
            })
    }
    fun clear(){
        photoPath=""
        plant=""
    }
}