package com.st.supjournal

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.st.supjournal.constance.CONSTANCE
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
        jwToken = sharedPref.getString(CONSTANCE.JWT, "").toString()

        /*
        Обрабатываем нажатие клавиши войти, выполняет http запрос на сервер
         */
        binding.signIn.setOnClickListener {
            authViewModel.apiAuth()
        }

        /**
         * Наблюдаем за liveData
         */
        authViewModel.authStatus.observe(this) {
            if (authViewModel.authStatus.value == true) {
                startMain()
            }
        }
    }

    /**
     * Запускаем основную активность
     */
    private fun startMain(){
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

/**
 * ViewModel для AuthActivity
 */
class AuthViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var binding: ActivityAuthBinding
    private val app = getApplication<Application?>()
    var authStatus = MutableLiveData(false)

    fun init (binding: ActivityAuthBinding) {
        this.binding = binding
    }

    /**
     * Запрос к базе данных (Передаем логин пароль). Проверяем ответ.
     * Если ок -> пишем токент в шаредпреф, пишем в лайфдату успешную авторизацию
     * (активити видя это запускает основное).
     * Если не ок -> сообщаем юзеру что не так.
     */
    fun apiAuth(){
        if (inputCheck()) {
            var res: AuthResponse
            val login = binding.editLogin.text.toString()
            val pass = binding.editPass.text.toString()
            viewModelScope.launch {
                try {
                    res = ApiService.retrofitService.auth(AuthBody(login, pass)).body()!!
                    if (res.status == "ok") {
                        addDataSharedPref(CONSTANCE.JWT, res.token.toString())
                        authStatus.value = true
                    } else {
                        binding.test.text = res.msg
                        when (res.status) {
                            "incorrect_user" -> binding.editLogin.error = res.msg
                            "incorrect_password" -> binding.editPass.error = res.msg
                        }
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

    /**
     * Добавляет данные в SharedPref
     */
    private fun addDataSharedPref(key: String, value: String) {
        val sharedPref = app.getSharedPreferences("App_Pref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }
}
