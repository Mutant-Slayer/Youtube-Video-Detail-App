package com.example.videodetailing.network

import com.example.videodetailing.model.VideoDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IAPIService {
    @GET("youtube/v3/videos/")
    suspend fun getDetails(
        @Query("part") part: String,
        @Query("id") videoId: String,
        @Query("key") apiKey: String
    ): Response<VideoDetails>
}