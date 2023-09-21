package com.example.weathernow.util

object Constants {
    val PREF_FILE_NAME = "Location_Pref"
    val CITY_KEY = "city_key"
    val LAT_KEY = "lat_key"
    val LON_KEY = "lon_key"
    val BASE_URL = "https://api.openweathermap.org"
    val API_KEY = "f726710a17a6f2fc7cd27b4ab781920a"
    val LOCATION_PERMISSION = Array<String>(2){"android.permission.ACCESS_COARSE_LOCATION"; "android.permission.ACCESS_FINE_LOCATION"}
    val PERMISSION_ACCESS_LOCATION = 101
}