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

data class AuthBody(
    @SerializedName("user")
    val login: String,
    @SerializedName("pass_")
    val pass_: String
)

data class AuthResponse(
    @SerializedName("token")
    var token: String?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("msg")
    var msg: String?
)

interface ApiServiceService {
    /**
     * Передает на сервер логин и пароль, возвращает токен и данные юзера, либо сообщение об ошибке
     */
    @POST("api/mobile/auth")
    suspend fun auth (@Body authBody: AuthBody): Response<AuthResponse>
}

object ApiService {
    val retrofitService: ApiServiceService by lazy {
        retrofit.create(ApiServiceService::class.java)
    }
}
