package com.st.supjournal.network


import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


private const val BASE_URL = "http://10.0.2.2:5000"

private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

data class AuthReq(
    val user: String,
    val pass_: String
)

data class RegisterReq(
    val user: String,
    val pass_: String,
    val e_mail: String
)

data class AuthResponse(
    val token: String?,
    val user: String?,
    val user_id: String?
)

data class RegisterResponse(
    val token: String?,
    val user: String?,
    val user_id: String?,
    val msg: String?
)

interface ApiServiceService {
    /**
     * Передает на сервер логин и пароль, возвращает токен и данные юзера, либо сообщение об ошибке
     */
    @POST("api/mobile/auth")
    suspend fun auth (@Body authReq: AuthReq): Response<AuthResponse>

    /**
     * Запрос на регистрацию
     */
    @POST("api/mobile/register")
    suspend fun register(@Body registerReq: RegisterReq): Response<RegisterResponse>
}

object ApiService {
    val retrofitService: ApiServiceService by lazy {
        retrofit.create(ApiServiceService::class.java)
    }
}
