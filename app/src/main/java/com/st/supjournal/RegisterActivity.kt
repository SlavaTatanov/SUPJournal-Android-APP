package com.st.supjournal

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.st.supjournal.databinding.ActivityRegisterBinding
import com.st.supjournal.network.ApiService
import com.st.supjournal.network.RegisterReq
import com.st.supjournal.network.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Response

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
                resp = ApiService.retrofitService.register(RegisterReq(user, pass, email))
            }
        }
    }

    /**
     * Проверка пользовательского ввода
     */
    private fun inputCheck(): Boolean {
        var flag = true
        arrayOf(binding.regLogin, binding.regPass, binding.regPassRep, binding.regEM).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = app.getString(R.string.input_error)
                flag = false
            }
        }
        if (binding.regPass.text != binding.regPassRep.text) {
            arrayOf(binding.regPassRep, binding.regPass).forEach {
                it.error = app.getString(R.string.password_not_equal)
            }
            flag = false
        }
        return flag
    }
}