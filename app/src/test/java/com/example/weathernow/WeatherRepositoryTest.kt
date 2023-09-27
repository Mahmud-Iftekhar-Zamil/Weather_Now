package com.example.weathernow

import android.content.SharedPreferences
import com.example.weathernow.repository.WeatherRepository
import com.example.weathernow.webservice.WeatherApiClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import java.lang.IndexOutOfBoundsException
import java.net.HttpURLConnection

class WeatherRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK lateinit var sut: WeatherRepository
    @MockK lateinit var sharedPref: SharedPreferences
    @MockK lateinit var mockApi: WeatherApiClient

    @Before
    fun setUp() {
        sharedPref = mockk()
        mockApi = mockk()
        sut = WeatherRepository(mockApi, sharedPref)
    }

    @Test
    fun test_getLocationResponse_Success() = runTest{
        coEvery { sut.getLocationResponse("Buffalo") } returns DI.SuccessLocationResponse
        val res = sut.getLocationResponse("Buffalo")
        Assert.assertEquals("Buffalo",res.name)
    }

    @Test
    fun test_getLocationResponse_Error() = runTest{
        coEvery { sut.getLocationResponse("Buffalo") } returns DI.ErrorLocationResponse
        val res = sut.getLocationResponse("Buffalo")
        Assert.assertEquals(true,res.name.isEmpty())
    }

    @Test
    fun test_getCityFromCoordinate_Success() = runTest{
        coEvery { mockApi.getReverseLocationResponse(42.8867166, -78.8783922, "ABC").body() } returns DI.SuccessRevLocationRes
        coEvery { sut.getCityFromCoordinate(42.8867166, -78.8783922) } returns "Buffalo"
        val res = sut.getCityFromCoordinate(42.8867166, -78.8783922)
        Assert.assertEquals("Buffalo",res)
    }

    @Test
    fun test_getCityFromCoordinate_Error() = runTest{
        coEvery { mockApi.getReverseLocationResponse(42.8867166, -78.8783922, "ABC").body() } returns DI.ErrorRevLocationRes
        coEvery { sut.getCityFromCoordinate(42.8867166, -78.8783922) } returns ""
        val res = sut.getCityFromCoordinate(42.8867166, -78.8783922)
        Assert.assertEquals(true,res.isEmpty())
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}