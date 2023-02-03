package com.st.supjournal

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.st.supjournal.databinding.ActivityAuthBinding
import com.st.supjournal.network.ApiViewModel


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private lateinit var sharedPref: SharedPreferences
    private lateinit var jwToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Получаем доступ к внутреннему хранилищу и забираем из него JWT токен
         */
        sharedPref = getSharedPreferences("App_Pref", MODE_PRIVATE)
        jwToken = sharedPref.getString("JWT", "").toString()

        /*
        Обрабатываем нажатие клавиши войти, выполняет http запрос на сервер
         */
        binding.btn.setOnClickListener {
            val login = binding.editLogin.text.toString()
            val pass = binding.editPass.text.toString()
            ApiViewModel().apiAuth(login, pass, binding)
        }
    }
}
