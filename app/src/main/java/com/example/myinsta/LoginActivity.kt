package com.example.myinsta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myinsta.databinding.ActivityLoginBinding
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if(email == "user@example.com" && password == "123456789"){
                userPrefs.edit { putBoolean("isLoggedIn", true) }
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
            }
        }
        setContentView(binding.root)
    }
}