package com.st.supjournal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.st.supjournal.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Кнопка назад
        binding.regBackBtn.setOnClickListener { finish() }
    }
}