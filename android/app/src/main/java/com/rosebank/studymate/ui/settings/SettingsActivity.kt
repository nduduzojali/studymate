package com.rosebank.studymate.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rosebank.studymate.databinding.ActivitySettingsBinding
import com.rosebank.studymate.model.UpdateSettingsRequest
import com.rosebank.studymate.network.RetrofitClient
import com.rosebank.studymate.ui.login.LoginActivity
import com.rosebank.studymate.util.InputValidator
import com.rosebank.studymate.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "SettingsActivity"

// Implements the "user must be able to change their settings in the app"
// requirement: the user can update their display name and/or password.
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.nameInput.setText(sessionManager.getUserName())

        binding.saveSettingsButton.setOnClickListener { attemptSaveSettings() }
        binding.logoutButton.setOnClickListener {
            sessionManager.clear()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun attemptSaveSettings() {
        val name = binding.nameInput.text.toString().trim()
        val newPassword = binding.newPasswordInput.text.toString()

        if (!InputValidator.isNonEmpty(name)) {
            binding.nameInput.error = "Name cannot be empty"
            return
        }
        if (newPassword.isNotEmpty() && !InputValidator.isValidPassword(newPassword)) {
            binding.newPasswordInput.error = "Password must be at least 6 characters"
            return
        }

        setLoading(true)
        Log.i(TAG, "Saving settings changes")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updateProfile(
                    sessionManager.getBearerToken(),
                    UpdateSettingsRequest(
                        fullName = name,
                        newPassword = newPassword.ifEmpty { null }
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    sessionManager.saveUserName(response.body()!!.fullName)
                    Toast.makeText(this@SettingsActivity, "Settings saved.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.w(TAG, "Failed to save settings: HTTP ${response.code()}")
                    Toast.makeText(this@SettingsActivity, "Couldn't save settings.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error saving settings", e)
                Toast.makeText(this@SettingsActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error saving settings", e)
                Toast.makeText(this@SettingsActivity, "Something went wrong.", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.settingsProgress.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        binding.saveSettingsButton.isEnabled = !isLoading
    }
}
