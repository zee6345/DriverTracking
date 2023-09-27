package com.app.drivertracking.data.models.response

sealed class DataSate<out T> {
    object Loading : DataSate<Nothing>()
    data class Success<T>(val response: T) : DataSate<T>()
    data class Error(val error: String) : DataSate<Nothing>()
}
