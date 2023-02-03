package com.st.supjournal.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.st.supjournal.databinding.ActivityAuthBinding
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_URL = "http://10.0.2.2:5000"

private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

data class AuthBody(
    @SerializedName("user")
    val login: String,
    @SerializedName("pass_")
    val pass_: String
)

data class AuthResponse(
    @SerializedName("token")
    var token: String?
)


interface ApiServiceService {
    @POST("api/mobile/auth")
    suspend fun auth (@Body authBody: AuthBody): Response<AuthResponse>
}

object ApiService {
    val retrofitService: ApiServiceService by lazy {
        retrofit.create(ApiServiceService::class.java)
    }
}

/**
 * Класс предоставляет доступ к API сервера, и реализует функции http запросов
 */
class ApiViewModel: ViewModel() {

    fun apiAuth(login: String, pass_: String, binding: ActivityAuthBinding) {
        val res = AuthResponse("1")
        viewModelScope.launch {
            res.token = ApiService.retrofitService.auth(AuthBody(login, pass_)).body()?.token
            binding.test.text = res.token
        }
    }
}
