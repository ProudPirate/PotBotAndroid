package com.plantdisease.plantdiseasedetection.utils

import java.io.Serializable

class ResultState<T>
private constructor(
    val status: Status,
    var data: T?,
    val message: String?
) : Serializable {

    companion object {

        fun <T> success(data: T?): ResultState<T> {
            return ResultState(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String?, data: T? = null): ResultState<T> {
            return ResultState(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): ResultState<T> {
            return ResultState(Status.LOADING, data, null)
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, message=$message)"
    }
}