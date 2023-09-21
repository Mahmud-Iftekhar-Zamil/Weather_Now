package com.example.weathernow.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkConnectivityStatus(context: Context): LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var isConnected: Boolean = false
        private set


    val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
            isConnected = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
            isConnected = false
        }
    }

    private fun checkInternet(){

        val network = connectivityManager.activeNetwork
        if(network==null){
            postValue(false)
            isConnected = false
        }

        val requestBuilder = NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        }.build()

        connectivityManager.registerNetworkCallback(requestBuilder,networkStatusCallback)
    }

    override fun onActive() {
        super.onActive()
        checkInternet()
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkStatusCallback)
    }
}