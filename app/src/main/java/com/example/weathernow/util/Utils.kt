package com.example.weathernow.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

object Utils {

    private val directions = Array(8) { "north";"northeast"; "east"; "southeast"; "south"; "southwest"; "west"; "northwest"}

    fun getDateTimeFromEpoch(timestamp: Long): String {
        return SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(Date(timestamp*1000)).toString()
    }

    fun getTimeFromEpoch(timestamp: Long): String {
        return SimpleDateFormat("hh:mm a").format(Date(timestamp*1000)).toString()
    }

    fun getDirectionFromDegree(degree: Int): String {
        var degrees = degree * 8 / 360;
        degrees = Math.round(degrees.toDouble()).toInt()
        degrees = (degrees + 8) % 8
        return directions[degrees]
    }

    fun getIconURL(icon: String): String {
        return "https://openweathermap.org/img/wn/$icon@4x.png"
    }
}