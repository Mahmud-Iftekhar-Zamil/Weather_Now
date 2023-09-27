package com.example.weathernow.webservice.response.location

data class LocationResponse(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String
)