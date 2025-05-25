package com.example.videodetailing.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videodetailing.model.VideoDetails
import com.example.videodetailing.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = MainRepository(ApiClient.apiService)

    private val _videoDetails = MutableStateFlow<UiState<VideoDetails>?>(null)
    val videoDetails: StateFlow<UiState<VideoDetails>?> = _videoDetails

    fun getVideoDetails(videoId: String) {
        viewModelScope.launch {
            _videoDetails.value = UiState.Loading

            try {
                val result = repository.getDetails(videoId)
                _videoDetails.value = UiState.Success(result)
            } catch (e: Exception) {
                _videoDetails.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}