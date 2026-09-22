package com.rosebank.studymate.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rosebank.studymate.databinding.ActivityLoginBinding
import com.rosebank.studymate.model.LoginRequest
import com.rosebank.studymate.network.RetrofitClient
import com.rosebank.studymate.ui.dashboard.DashboardActivity
import com.rosebank.studymate.ui.register.RegisterActivity
import com.rosebank.studymate.util.InputValidator
import com.rosebank.studymate.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // If we already have a saved token, skip straight to the dashboard.
        if (sessionManager.isLoggedIn()) {
            goToDashboard()
            return
        }

        binding.loginButton.setOnClickListener { attemptLogin() }
        binding.goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        // Validate locally first so a bad/empty input never even reaches
        // the network layer — this is what keeps the app from crashing on
        // invalid input, per the brief's requirement.
        if (!InputValidator.isValidEmail(email)) {
            binding.emailInput.error = "Enter a valid email address"
            return
        }
        if (!InputValidator.isNonEmpty(password)) {
            binding.passwordInput.error = "Password is required"
            return
        }

        setLoading(true)
        Log.i(TAG, "Attempting login for $email")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Log.i(TAG, "Login succeeded for ${body.user.email}")
                    sessionManager.saveToken(body.token)
                    sessionManager.saveUserName(body.user.fullName)
                    goToDashboard()
                } else {
                    Log.w(TAG, "Login failed with HTTP ${response.code()}")
                    Toast.makeText(this@LoginActivity, "Invalid email or password.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                // Network/connectivity failure — handled gracefully rather
                // than crashing the app.
                Log.e(TAG, "Network error during login", e)
                Toast.makeText(this@LoginActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during login", e)
                Toast.makeText(this@LoginActivity, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.loginProgress.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        binding.loginButton.isEnabled = !isLoading
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
