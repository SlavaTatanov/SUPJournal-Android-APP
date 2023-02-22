package com.st.supjournal

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.st.supjournal.constance.CONSTANCE
import com.st.supjournal.databinding.ActivityRegisterBinding
import com.st.supjournal.network.ApiService
import com.st.supjournal.network.RegisterReq
import com.st.supjournal.network.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.ConnectException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Запускаем ViewModel
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        registerViewModel.init(binding)

        // Кнопка регистрация
        binding.regBtn.setOnClickListener { registerViewModel.regApi()}

        // Кнопка назад
        binding.regBackBtn.setOnClickListener { finish() }
    }
}

class RegisterViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var binding: ActivityRegisterBinding
    private val app = getApplication<Application?>()

    fun init (binding: ActivityRegisterBinding) {
        this.binding = binding
    }

    /**
     * Запрос регистрации на серевер, отправляем логин, пароль и почту
     */
    fun regApi(){
        binding.regErrTV.text = ""
        // Проверка пользовательского ввода и сбор данных из полей
        if (inputCheck()) {
            var resp: Response<RegisterResponse>
            val user = binding.regLogin.text.toString()
            val pass = binding.regPass.text.toString()
            val email = binding.regEM.text.toString()

            // Запрос к API
            viewModelScope.launch {
                try {
                    resp = ApiService.retrofitService.register(RegisterReq(user, pass, email))
                    // Пришел код 200, берем данные и пешем в sharedPref
                    if (resp.code() == 200){
                        val res = resp.body()!!
                        addDataSharedPref(CONSTANCE.JWT, res.token.toString())
                        addDataSharedPref(CONSTANCE.user, res.user.toString())
                        addDataSharedPref(CONSTANCE.user_id, res.user_id.toString())
                    }
                    // Пришел код 400, проверяем что это не валидная почта, сообщаем юзеру
                    else if (resp.code() == 400){
                        val err = resp.headers().get("X-Error-Message")
                        if (err == "invalid_email") {
                            binding.regEM.error = app.getString(R.string.incorrect_email)
                        }
                    }
                    // Пришел код 409, смотрим ошибку, сообщаем юзеру
                    else if (resp.code() == 409) {
                        when (resp.headers().get("X-Error-Message")) {
                            "incorrect_user" -> {
                                binding.regLogin.error = app.getString(R.string.user_exists)
                            }
                        }
                    }
                }
                // Ловим отсутствие подключения к серверу
                catch (e: ConnectException) {
                    binding.regErrTV.text = app.getString(R.string.connection_error)
                }
            }
        }
    }

    /**
     * Добавляет данные в SharedPref
     */
    private fun addDataSharedPref(key: String, value: String) {
        val sharedPref = app.getSharedPreferences("App_Pref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * Проверка пользовательского ввода
     */
    private fun inputCheck(): Boolean {
        var flag = true
        // Проверка что поля не пустые
        arrayOf(binding.regLogin, binding.regPass, binding.regPassRep, binding.regEM).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = app.getString(R.string.input_error)
                flag = false
            }
        }
        // Проверка что пароли совпадают
        if (binding.regPass.text.toString() != binding.regPassRep.text.toString()) {
            arrayOf(binding.regPassRep, binding.regPass).forEach {
                it.error = app.getString(R.string.password_not_equal)
            }
            flag = false
        }
        return flag
    }
}