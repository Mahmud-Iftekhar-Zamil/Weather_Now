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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWeatherBinding.bind(view)
        val menuHost: MenuHost = requireActivity()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weatherData.collect {resultInfo ->
                    when(resultInfo) {
                        is ResultInfo.Loading -> {}
                        is ResultInfo.Success -> {
                            val _data = resultInfo.data
                            Log.d(TAG,"Weather Data = $_data")
                            binding.apply {
                                textViewCity.text = _data?.city
                                textViewWeatherCondition.text = "${_data?.main}"
                                textViewTemperatureRange.text = "High: ${_data?.temp_max}\u2103   Low: ${_data?.temp_min}\u2103"
                                textViewFeelslike.text = "${_data?.feels_like}\u2103"
                                textViewHumidity.text = "${_data?.humidity} %"
                                textViewSunrise.text = _data?.sunrise
                                textViewSunset.text = _data?.sunset
                                textViewTemperature.text = "${_data?.temp}\u2103"
                                textViewWind.text = "${_data?.speed} mph | ${_data?.deg}"
                                imageViewIcon.load(_data?.icon)
                                textViewVisibility.text = "${_data?.visibility} km"
                            }
                        }
                        is ResultInfo.Error -> {
                            Snackbar.make(binding.root,resultInfo.error, Snackbar.LENGTH_LONG).show()
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


