package com.example.weathernow.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import com.example.weathernow.model.data.WeatherData
import com.example.weathernow.util.Constants
import com.example.weathernow.util.ResultInfo
import com.example.weathernow.util.Utils
import com.example.weathernow.webservice.WeatherApiClient
import com.example.weathernow.webservice.response.location.LocationResponse
import com.example.weathernow.webservice.response.location.LocationResponseItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiClient: WeatherApiClient,
    private val sharedPref: SharedPreferences
) {
    private val TAG = "WeatherRepository"
    private var mLatitute: Double = 0.0
    private var mLongitude: Double = 0.0

    private suspend fun getCityFromCoordinate(lat: Double, lon: Double): String {
        val res = weatherApiClient.getReverseLocationResponse(lat,lon,Constants.API_KEY)
        Log.d(TAG,"getCityFromCoordinate() : ${res[0].name}")
        return res[0].name
    }

    private suspend fun getLocationData(city: String): LocationResponse {
        return weatherApiClient.getLocationResponse("$city,US", Constants.API_KEY)
    }

    suspend fun fetchData(queryString: String): ResultInfo<WeatherData> {
        var mCity: String = queryString
        try{
            if(mCity == "" && !sharedPref.contains(Constants.CITY_KEY) && sharedPref.contains(Constants.LAT_KEY)){
                mLatitute = sharedPref.getFloat(Constants.LAT_KEY,0F).toDouble()
                mLongitude = sharedPref.getFloat(Constants.LON_KEY,0F).toDouble()
                mCity = getCityFromCoordinate(mLatitute,mLongitude)
                sharedPref.edit().putString(Constants.CITY_KEY,mCity).apply()
            } else if(mCity == "" && sharedPref.contains(Constants.CITY_KEY)) {
                mCity = sharedPref.getString(Constants.CITY_KEY,"")!!
            }
            val locationResponse = getLocationData(mCity)
            mLatitute = locationResponse[0].lat
            mLongitude = locationResponse[0].lon

            sharedPref.edit().putString(Constants.CITY_KEY,mCity).apply()
            sharedPref.edit().putFloat(Constants.LAT_KEY, mLatitute.toFloat()).apply()
            sharedPref.edit().putFloat(Constants.LON_KEY, mLongitude.toFloat()).apply()

            val weatherResponse = weatherApiClient.getWeatherResponse(mLatitute, mLongitude,Constants.API_KEY)
            Log.d(TAG, "DATA = $weatherResponse")
            fun processData(): WeatherData {
                val wData = WeatherData()
                wData.country = locationResponse[0].country
                wData.lat = locationResponse[0].lat
                wData.lon = locationResponse[0].lon
                wData.city = locationResponse[0].name
                wData.state = locationResponse[0].state
                wData.clouds = weatherResponse.clouds.all
                wData.feels_like = weatherResponse.main.feels_like
                wData.humidity = weatherResponse.main.humidity
                wData.temp = weatherResponse.main.temp
                wData.deg = Utils.getDirectionFromDegree(weatherResponse.wind.deg)
                wData.description = weatherResponse.weather[0].description
                wData.icon = Utils.getIconURL(weatherResponse.weather[0].icon)
                wData.pressure = weatherResponse.main.pressure
                wData.speed = weatherResponse.wind.speed
                wData.temp_max = weatherResponse.main.temp_max
                wData.temp_min = weatherResponse.main.temp_min
                wData.main = weatherResponse.weather[0].main
                wData.sunrise = Utils.getTimeFromEpoch(weatherResponse.sys.sunrise)
                wData.sunset = Utils.getTimeFromEpoch(weatherResponse.sys.sunset)
                wData.dt = Utils.getDateTimeFromEpoch(weatherResponse.dt)
                wData.visibility = weatherResponse.visibility/1000
                return wData
            }
            return ResultInfo.Success(processData())
        }catch (ex: Exception) {
            Log.e(TAG,"Error: ${ex.message.toString()}")
            return ResultInfo.Error(ex.message.toString())
        }
    }
}