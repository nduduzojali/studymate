package com.rosebank.studymate.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rosebank.studymate.databinding.ActivityRegisterBinding
import com.rosebank.studymate.model.RegisterRequest
import com.rosebank.studymate.network.RetrofitClient
import com.rosebank.studymate.ui.dashboard.DashboardActivity
import com.rosebank.studymate.util.InputValidator
import com.rosebank.studymate.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.registerButton.setOnClickListener { attemptRegister() }
        binding.goToLogin.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        if (!InputValidator.isNonEmpty(name)) {
            binding.nameInput.error = "Please enter your full name"
            return
        }
        if (!InputValidator.isValidEmail(email)) {
            binding.emailInput.error = "Enter a valid email address"
            return
        }
        if (!InputValidator.isValidPassword(password)) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            return
        }

        setLoading(true)
        Log.i(TAG, "Attempting registration for $email")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(fullName = name, email = email, password = password)
                )
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.i(TAG, "Registration succeeded for ${body.user.email}")
                    sessionManager.saveToken(body.token)
                    sessionManager.saveUserName(body.user.fullName)
                    startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                    finish()
                } else if (response.code() == 409) {
                    Toast.makeText(this@RegisterActivity, "An account with this email already exists.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w(TAG, "Registration failed with HTTP ${response.code()}")
                    Toast.makeText(this@RegisterActivity, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error during registration", e)
                Toast.makeText(this@RegisterActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during registration", e)
                Toast.makeText(this@RegisterActivity, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.registerProgress.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        binding.registerButton.isEnabled = !isLoading
    }
}
