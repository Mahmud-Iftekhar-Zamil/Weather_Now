package com.example.weathernow.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.weathernow.model.data.WeatherData
import com.example.weathernow.util.Constants
import com.example.weathernow.util.ResultInfo
import com.example.weathernow.util.Utils
import com.example.weathernow.webservice.WeatherApiClient
import com.example.weathernow.webservice.response.location.LocalNames
import com.example.weathernow.webservice.response.location.LocationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiClient: WeatherApiClient,
    private val sharedPref: SharedPreferences
) {
    private val TAG = "WeatherRepository"
    private var mLatitute: Double = 0.0
    private var mLongitude: Double = 0.0

    private val _weatherDataFlow = MutableStateFlow<ResultInfo<WeatherData>>(ResultInfo.Loading())
    val weatherDataFlow: StateFlow<ResultInfo<WeatherData>>
        get() = _weatherDataFlow

    suspend fun getCityFromCoordinate(lat: Double, lon: Double): String {
        val response = weatherApiClient.getReverseLocationResponse(lat,lon,Constants.API_KEY)
        Log.d(TAG, "getCityFromCoordinate = $response")
        return response.body()?.get(0)?.name ?: ""
    }

    suspend fun getLocationResponse(city: String): LocationResponse {
        val response = weatherApiClient.getLocationResponse("$city,US", Constants.API_KEY)
        return response.body()!![0]
    }

    suspend fun fetchData(queryString: String) {
        var mCity: String = queryString
        try{
            if(mCity == "" && !sharedPref.contains(Constants.CITY_KEY) && sharedPref.contains(Constants.LAT_KEY)){
                mLatitute = sharedPref.getFloat(Constants.LAT_KEY,0F).toDouble()
                mLongitude = sharedPref.getFloat(Constants.LON_KEY,0F).toDouble()
                Log.d(TAG, "Execute line")
                mCity = getCityFromCoordinate(mLatitute,mLongitude)
                Log.d(TAG,"city = $mCity")
                sharedPref.edit().putString(Constants.CITY_KEY,mCity).apply()
            } else if(mCity == "" && sharedPref.contains(Constants.CITY_KEY)) {
                mCity = sharedPref.getString(Constants.CITY_KEY,"")!!
            }
            Log.d(TAG,"getLocationResponse(mCity) = $mCity")
            val locationResponse = getLocationResponse(mCity)
            mLatitute = locationResponse.lat
            mLongitude = locationResponse.lon

            sharedPref.edit().putString(Constants.CITY_KEY,mCity).apply()
            sharedPref.edit().putFloat(Constants.LAT_KEY, mLatitute.toFloat()).apply()
            sharedPref.edit().putFloat(Constants.LON_KEY, mLongitude.toFloat()).apply()

            val response = weatherApiClient.getWeatherResponse(mLatitute, mLongitude,Constants.API_KEY)
            val weatherResponse = response.body()!!
            Log.d(TAG, "DATA = $response")

            fun processData(): WeatherData {
                val wData = WeatherData()
                wData.country = locationResponse.country
                wData.lat = locationResponse.lat
                wData.lon = locationResponse.lon
                wData.city = locationResponse.name
                wData.state = locationResponse.state
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
            _weatherDataFlow.emit(ResultInfo.Success(processData()))
        }catch (ex: Exception) {
            Log.e(TAG,"Error: ${ex.message.toString()}")
            _weatherDataFlow.emit(ResultInfo.Error(ex.message.toString()))
        }
    }
}