package com.example.weathernow

import com.example.weathernow.webservice.WeatherApiClient
import com.example.weathernow.webservice.response.location.LocalNames
import com.example.weathernow.webservice.response.location.LocationResponse
import com.example.weathernow.webservice.response.reverselocation.ReverseLocationResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object DI {
    val mockWebServer = MockWebServer()

    val weatherApiClientMock = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiClient::class.java)

    val city_to_lat_lon_success_json = "[\n" +
            "{\n" +
            "\"name\": \"Buffalo\",\n" +
            "\"local_names\": {\n" +
            "\"en\": \"Buffalo\",\n" +
            "\"ru\": \"Буффало\",\n" +
            "\"uk\": \"Баффало\",\n" +
            "\"ar\": \"بوفالو\",\n" +
            "\"oc\": \"Buffalo\"\n" +
            "},\n" +
            "\"lat\": 42.8867166,\n" +
            "\"lon\": -78.8783922,\n" +
            "\"country\": \"US\",\n" +
            "\"state\": \"New York\"\n" +
            "}\n" +
            "]"

    val city_to_lat_lon_error_json = "[]"

    val lat_lon_to_city_success_json = "[\n" +
            "{\n" +
            "\"name\": \"Buffalo\",\n" +
            "\"local_names\": {\n" +
            "\"oc\": \"Buffalo\",\n" +
            "\"en\": \"Buffalo\",\n" +
            "\"ru\": \"Буффало\",\n" +
            "\"uk\": \"Баффало\",\n" +
            "\"ar\": \"بوفالو\"\n" +
            "},\n" +
            "\"lat\": 42.8867166,\n" +
            "\"lon\": -78.8783922,\n" +
            "\"country\": \"US\",\n" +
            "\"state\": \"New York\"\n" +
            "}\n" +
            "]"

    val lat_lon_to_city_error_json = "{\n" +
            "\"cod\": \"400\",\n" +
            "\"message\": \"Nothing to geocode\"\n" +
            "}"

    val SuccessRevLocationRes: List<ReverseLocationResponse> = listOf(ReverseLocationResponse("US",42.8867166,com.example.weathernow.webservice.response.reverselocation.LocalNames("","","","","","","",""),-78.8783922,"Buffalo", "New York"))
    val ErrorRevLocationRes:List<ReverseLocationResponse> = listOf(ReverseLocationResponse("",0.0,com.example.weathernow.webservice.response.reverselocation.LocalNames("","","","","","","",""),0.0,"", ""))
    val SuccessLocationResponse = LocationResponse("US",42.8867166, LocalNames("","","","",""), -78.8783922, "Buffalo", "New York")
    val ErrorLocationResponse = LocationResponse("",0.0, LocalNames("","","","",""),0.0, "", "")

}