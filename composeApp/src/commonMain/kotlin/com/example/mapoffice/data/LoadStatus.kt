package com.example.mapoffice.data

sealed class LoadStatus {
    object Initial : LoadStatus()
    object Loading : LoadStatus()
    object Success : LoadStatus()
    data class Error(val message: String) : LoadStatus()
}