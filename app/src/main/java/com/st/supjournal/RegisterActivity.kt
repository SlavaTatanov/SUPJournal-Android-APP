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
        if (inputCheck()) {
            var resp: Response<RegisterResponse>
            val user = binding.regLogin.text.toString()
            val pass = binding.regPass.text.toString()
            val email = binding.regEM.text.toString()

            viewModelScope.launch {
                try {
                    resp = ApiService.retrofitService.register(RegisterReq(user, pass, email))
                    if (resp.code() == 200){
                        val res = resp.body()!!
                        addDataSharedPref(CONSTANCE.JWT, res.token.toString())
                        addDataSharedPref(CONSTANCE.user, res.user.toString())
                        addDataSharedPref(CONSTANCE.user_id, res.user_id.toString())
                    } else if (resp.code() == 400){
                        TODO("Нужно реализовать проверку что email не валидный")
                    } else if (resp.code() == 409) {
                        TODO("Нужно реализовать проверку что user существует")
                    }
                } catch (e: ConnectException) {
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