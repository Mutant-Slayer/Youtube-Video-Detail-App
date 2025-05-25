package com.example.videodetailing.data

import com.example.videodetailing.BuildConfig
import com.example.videodetailing.model.VideoDetails
import com.example.videodetailing.network.IAPIService

class MainRepository(private val apiService: IAPIService) {
    suspend fun getDetails(videoId: String): VideoDetails {
        return try {
            val response = apiService.getDetails(
                part = "snippet,statistics,contentDetails",
                videoId = videoId,
                apiKey = BuildConfig.API_KEY
            )

            if (response.isSuccessful) {
                val apiResponse = response.body()
                apiResponse ?: throw Exception("Video not found")
            } else {
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
}