package com.example.weathernow

import com.example.weathernow.webservice.WeatherApiClient
import io.mockk.junit4.MockKRule
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class WeatherApiClientTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var mockWeatherApiClient: WeatherApiClient
    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockResponse: MockResponse

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockResponse = MockResponse()
        mockWeatherApiClient = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiClient::class.java)
    }

    @Test
    fun test_getLocationResponse_Success() = runTest{
        mockResponse.apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(DI.city_to_lat_lon_success_json)
        }
        mockWebServer.enqueue(mockResponse)
        val res = mockWeatherApiClient.getLocationResponse("Buffalo", "ABC")
        mockWebServer.takeRequest()
        Assert.assertEquals("Buffalo",res.body()!![0].name)
    }

    @Test
    fun test_getLocationResponse_Error() = runTest{
        mockResponse.apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(DI.city_to_lat_lon_error_json)
        }
        mockWebServer.enqueue(mockResponse)
        val res = mockWeatherApiClient.getLocationResponse("Buffalo", "ABC")
        mockWebServer.takeRequest()
        Assert.assertEquals(true,res.body()?.isEmpty())
    }

    @Test
    fun test_getCityFromCoordinate_Success() = runTest{
        mockResponse.apply {
            setResponseCode(HttpURLConnection.HTTP_OK)
            setBody(DI.lat_lon_to_city_success_json)
        }
        mockWebServer.enqueue(mockResponse)
        val res = mockWeatherApiClient.getReverseLocationResponse(42.8867166, -78.8783922, "ABC")
        mockWebServer.takeRequest()
        Assert.assertEquals("Buffalo",res.body()!![0].name)
    }

    @Test
    fun test_getCityFromCoordinate_Error() = runTest{
        mockResponse.apply {
            setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
            setBody(DI.lat_lon_to_city_error_json)
        }
        mockWebServer.enqueue(mockResponse)
        val res = mockWeatherApiClient.getReverseLocationResponse(42.8867166, -78.8783922, "ABC")
        mockWebServer.takeRequest()
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST,res.code())
    }


    @After
    fun tearDown() {
        mockWebServer.shutdown()
        unmockkAll()
    }
}