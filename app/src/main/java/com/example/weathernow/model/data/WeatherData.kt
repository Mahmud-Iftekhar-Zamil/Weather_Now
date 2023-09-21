package com.example.weathernow.model.data

import com.example.weathernow.webservice.response.weather.Wind
import java.time.Instant
import java.time.ZoneId


data class WeatherData(
    var clouds: Int?=0,
    var feels_like: Double?=0.0,
    var humidity: Int? = 0,
    var pressure: Int?= 0,
    var temp: Double?= 0.0,
    var temp_max: Double? = 0.0,
    var temp_min: Double? =0.0,
    var country: String? = "",
    var sunrise: String? = "",
    var sunset: String? = "",
    var description: String? = "",
    var icon: String? = "",
    var main: String? = "",
    var deg: String? = "",
    var speed: Double? = 0.0,
    var lat: Double? = 0.0,
    var lon: Double? = 0.0,
    var city: String? = "",
    var state: String? = "",
    var dt: String? = "",
    var visibility: Int? = 0
) {
}
