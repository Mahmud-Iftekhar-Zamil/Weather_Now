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

    init {
        fetchData()
    }

    private val _channel = Channel<ResultInfo<WeatherData>>()
    val channel = _channel.receiveAsFlow()

    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData>
        get() = _weatherData

    fun fetchData(queryString: String = "") = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG,"fetchData() is called")
        val weatherResponse = weatherRepository.fetchData(queryString)
        weatherResponse.data?.let {
            _weatherData.postValue(it)
        }

        _channel.send(weatherResponse)
    }
}