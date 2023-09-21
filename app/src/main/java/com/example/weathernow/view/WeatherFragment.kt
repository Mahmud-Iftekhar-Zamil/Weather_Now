package com.example.weathernow.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load

import com.example.weathernow.R
import com.example.weathernow.databinding.FragmentWeatherBinding
import com.example.weathernow.util.NetworkConnectivityStatus
import com.example.weathernow.util.ResultInfo
import com.example.weathernow.viewmodel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherFragment: Fragment(R.layout.fragment_weather) {
    private val TAG = "WeatherFragment"
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var connectivityStatus: NetworkConnectivityStatus

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWeatherBinding.bind(view)
        val menuHost: MenuHost = requireActivity()

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            Log.d(TAG,"Weather Data = $weatherData")
            binding.apply {
                textViewCity.text = weatherData.city
                textViewWeatherCondition.text = "${weatherData.main}"
                textViewTemperatureRange.text = "High: ${weatherData.temp_max}\u2103   Low: ${weatherData.temp_min}\u2103"
                textViewFeelslike.text = "${weatherData.feels_like}\u2103"
                textViewHumidity.text = "${weatherData.humidity} %"
                textViewSunrise.text = weatherData.sunrise
                textViewSunset.text = weatherData.sunset
                textViewTemperature.text = "${weatherData.temp}\u2103"
                textViewWind.text = "${weatherData.speed} mph | ${weatherData.deg}"
                imageViewIcon.load(weatherData.icon)
                textViewVisibility.text = "${weatherData.visibility} km"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channel.collect {
                    when(it) {
                        is ResultInfo.Loading -> {}
                        is ResultInfo.Success -> {}
                        is ResultInfo.Error -> {
                            Snackbar.make(binding.root,it.error, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        connectivityStatus = NetworkConnectivityStatus(requireContext())

        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.nav_menu,menu)
                val searchItem = menu.findItem(R.id.nav_menu_search)
                val searchView = searchItem.actionView as SearchView

                searchView.OnQueryTextSubmitted { queryString ->
                    searchView.clearFocus()
                    viewModel.fetchData(queryString)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.nav_menu_refresh -> {
                        viewModel.fetchData("")
                        return true
                    }
                    else -> true
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


    }
}

private fun SearchView.OnQueryTextSubmitted(listener: (String) -> Unit) {
    this.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            listener(query.orEmpty())
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }
    })
}


