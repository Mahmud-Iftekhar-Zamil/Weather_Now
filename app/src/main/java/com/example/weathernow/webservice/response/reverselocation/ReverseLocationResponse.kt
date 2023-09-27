package com.example.weathernow.webservice.response.reverselocation

data class ReverseLocationResponse(
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String,
    val state: String
)