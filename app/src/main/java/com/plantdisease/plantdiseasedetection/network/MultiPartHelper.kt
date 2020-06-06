package com.plantdisease.plantdiseasedetection.network

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun createStringRequestBody(string: String?): RequestBody {
    return (string ?: "").toRequestBody("text/plain".toMediaType())
}

fun prepareStringPart(
    partName: String, string: String?
): MultipartBody.Part {
    return MultipartBody.Part.createFormData(partName, string ?: "")
}

fun prepareFilePart(
    partName: String, fileUri: String?, fileName: String = ""
): MultipartBody.Part {
    if (fileUri.isNullOrEmpty()) return prepareStringPart(partName, fileUri)
    val file = File(fileUri)
    val requestFile = file.asRequestBody("application/octet-stream".toMediaType())
    val filename = if (fileName.isNotEmpty()) "$fileName.${file.extension}" else file.name
    return MultipartBody.Part.createFormData(partName, filename, requestFile)
}