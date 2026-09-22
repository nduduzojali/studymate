package com.rosebank.studymate.ui.addtask

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rosebank.studymate.databinding.ActivityAddTaskBinding
import com.rosebank.studymate.model.Task
import com.rosebank.studymate.network.RetrofitClient
import com.rosebank.studymate.util.InputValidator
import com.rosebank.studymate.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "AddTaskActivity"

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.saveTaskButton.setOnClickListener { attemptSaveTask() }
    }

    private fun attemptSaveTask() {
        val title = binding.titleInput.text.toString().trim()
        val module = binding.moduleInput.text.toString().trim()
        val priority = when (binding.priorityRadioGroup.checkedRadioButtonId) {
            binding.priorityLow.id -> "Low"
            binding.priorityHigh.id -> "High"
            else -> "Medium"
        }

        if (!InputValidator.isNonEmpty(title)) {
            binding.titleInput.error = "Task title is required"
            return
        }

        setLoading(true)
        Log.i(TAG, "Saving new task: $title")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createTask(
                    sessionManager.getBearerToken(),
                    Task(title = title, module = module, priority = priority)
                )
                if (response.isSuccessful) {
                    Log.i(TAG, "Task saved successfully")
                    Toast.makeText(this@AddTaskActivity, "Task saved.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.w(TAG, "Failed to save task: HTTP ${response.code()}")
                    Toast.makeText(this@AddTaskActivity, "Couldn't save task. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error saving task", e)
                Toast.makeText(this@AddTaskActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error saving task", e)
                Toast.makeText(this@AddTaskActivity, "Something went wrong.", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.saveTaskProgress.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        binding.saveTaskButton.isEnabled = !isLoading
    }
}
