package com.example.weathernow

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weathernow.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.internal.InjectedFieldSignature
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    @Inject
    lateinit var sharedPref: SharedPreferences

    private val TAG = "MainActivity"
    private lateinit var navController: NavController
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.show()

        setupActionBarWithNavController(navController, appBarConfiguration)
        getCurrentLocation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun getCurrentLocation() {
        val task: Task<Location> = fusedLocationProviderClient.getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY,null)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,Constants.LOCATION_PERMISSION,Constants.PERMISSION_ACCESS_LOCATION)
            return
        }
        task.addOnSuccessListener {
            if(it!=null){
                sharedPref.edit().putFloat(Constants.LAT_KEY,it.latitude.toFloat()).apply()
                sharedPref.edit().putFloat(Constants.LON_KEY,it.longitude.toFloat()).apply()
            }
            Log.d(TAG, "${it.latitude} , ${it.longitude}")
        }
    }
}