package com.st.supjournal

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.st.supjournal.databinding.ActivityAuthBinding
import com.st.supjournal.network.ApiService
import com.st.supjournal.network.AuthBody
import com.st.supjournal.network.AuthResponse
import kotlinx.coroutines.launch
import java.net.ConnectException


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var sharedPref: SharedPreferences
    private lateinit var jwToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.init(binding)

        /*
        Получаем доступ к внутреннему хранилищу и забираем из него JWT токен
        если он есть
         */
        sharedPref = getSharedPreferences("App_Pref", MODE_PRIVATE)
        jwToken = sharedPref.getString("JWT", "").toString()

        /*
        Обрабатываем нажатие клавиши войти, выполняет http запрос на сервер
         */
        binding.btn.setOnClickListener {
            authViewModel.apiAuth()
        }
        binding.test.text = jwToken
    }

    private fun startMain(){
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

/**
 * ViewModel для AuthActivity
 */
class AuthViewModel(application: Application): AndroidViewModel(application) {

    lateinit var binding: ActivityAuthBinding

    fun init (binding: ActivityAuthBinding) {
       this.binding = binding
    }

    /**
     * Запрос к базе данных (Передаем логин пароль)
     */
    fun apiAuth() {
        if (inputCheck()) {
            var res: AuthResponse
            val login = binding.editLogin.text.toString()
            val pass = binding.editPass.text.toString()
            viewModelScope.launch {
                try {
                    res = ApiService.retrofitService.auth(AuthBody(login, pass)).body()!!
                    if (res.msg == "ok") {
                        binding.test.text = res.token
                    } else {
                        binding.test.text = res.msg
                    }

                } catch (e: ConnectException) {
                    val err = getApplication<Application?>().getString(R.string.connection_error)
                    binding.test.text = err
                }
            }
        }

    }

    /**
     * Проверяем что поля login и password заполнены
     */
    private fun inputCheck (): Boolean{
        var flag = true
        for (it in arrayOf(binding.editLogin, binding.editPass)) {
                if (it.text.isNullOrEmpty()) {
                    it.error = getApplication<Application?>().getString(R.string.input_error)
                    flag = false
                }
            }
        return flag
    }
}

