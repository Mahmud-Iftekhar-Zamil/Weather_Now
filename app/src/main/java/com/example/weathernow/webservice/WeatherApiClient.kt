package com.example.weathernow.webservice

import com.example.weathernow.webservice.response.location.LocationResponse
import com.example.weathernow.webservice.response.reverselocation.ReverseLocationResponse
import com.example.weathernow.webservice.response.weather.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiClient {

    @GET("/geo/1.0/reverse?limit=1")
    suspend fun getReverseLocationResponse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String
    ): ReverseLocationResponse

    @GET("/geo/1.0/direct?")
    suspend fun getLocationResponse(
        @Query("q") city: String,
        @Query("appid") appid: String
    ): LocationResponse

    @GET("/data/2.5/weather?units=metric")
    suspend fun getWeatherResponse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String
    ): WeatherResponse
}