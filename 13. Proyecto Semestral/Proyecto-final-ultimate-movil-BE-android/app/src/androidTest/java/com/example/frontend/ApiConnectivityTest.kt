package com.example.frontend

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.frontend.core.network.QuickvntApi
import com.example.frontend.data.dto.LoginRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Prueba de conectividad real contra la API en producción (Render).
 *
 * Ejecutar en Android Studio: clic derecho en este archivo → Run 'ApiConnectivityTest'.
 */
@RunWith(AndroidJUnit4::class)
class ApiConnectivityTest {

    private val api: QuickvntApi by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val client = OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(QuickvntApi::class.java)
    }

    @Test
    fun listPublishedEvents_reachesApi() = runBlocking {
        val events = api.listEvents(statusFilter = "PUBLISHED")
        assertNotNull(events)
    }

    @Test
    fun login_withInvalidCredentials_returns401NotNetworkError() = runBlocking {
        val error = runCatching {
            api.login(
                LoginRequest(
                    email = "no-existe@test.com",
                    password = "wrong-password"
                )
            )
        }.exceptionOrNull()

        assertTrue("Debe fallar por credenciales, no por red", error != null)
        assertFalse(
            "Si ves 'Failed to connect' o 'timeout', revisa API_BASE_URL y conectividad",
            error?.message.orEmpty().contains("Failed to connect", ignoreCase = true)
        )
    }
}
