package com.example.weathernow.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathernow.model.data.WeatherData
import com.example.weathernow.repository.WeatherRepository
import com.example.weathernow.util.Constants
import com.example.weathernow.util.ResultInfo
import com.example.weathernow.webservice.response.weather.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.http.HTTP
import java.net.HttpURLConnection
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
)  : ViewModel() {
    private val TAG = "WeatherViewModel"

    val weatherData: StateFlow<ResultInfo<WeatherData>>
        get() = weatherRepository.weatherDataFlow

    init {
        fetchData()
    }

    fun fetchData(queryString: String = "") = viewModelScope.launch {
        weatherRepository.fetchData(queryString)
    }
}