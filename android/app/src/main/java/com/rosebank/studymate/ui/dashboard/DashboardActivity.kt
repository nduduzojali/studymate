package com.rosebank.studymate.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rosebank.studymate.databinding.ActivityDashboardBinding
import com.rosebank.studymate.model.Task
import com.rosebank.studymate.network.RetrofitClient
import com.rosebank.studymate.ui.addtask.AddTaskActivity
import com.rosebank.studymate.ui.login.LoginActivity
import com.rosebank.studymate.ui.settings.SettingsActivity
import com.rosebank.studymate.util.SessionManager
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "DashboardActivity"

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        setSupportActionBar(binding.toolbar)

        if (!sessionManager.isLoggedIn()) {
            goToLogin()
            return
        }

        adapter = TaskAdapter(
            onToggleComplete = { task -> toggleTaskComplete(task) },
            onDelete = { task -> deleteTask(task) }
        )
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { loadTasks() }
        binding.addTaskFab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(com.rosebank.studymate.R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.rosebank.studymate.R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            com.rosebank.studymate.R.id.action_logout -> {
                sessionManager.clear()
                goToLogin()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadTasks() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getTasks(sessionManager.getBearerToken())
                if (response.isSuccessful) {
                    val tasks = response.body() ?: emptyList()
                    Log.i(TAG, "Loaded ${tasks.size} tasks")
                    adapter.submitList(tasks)
                    binding.emptyStateText.visibility =
                        if (tasks.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                } else if (response.code() == 401) {
                    // Token expired/invalid — send the user back to Login.
                    sessionManager.clear()
                    goToLogin()
                } else {
                    Log.w(TAG, "Failed to load tasks: HTTP ${response.code()}")
                    Toast.makeText(this@DashboardActivity, "Couldn't load tasks.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network error loading tasks", e)
                Toast.makeText(this@DashboardActivity, "Couldn't reach the server. Check your connection.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading tasks", e)
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun toggleTaskComplete(task: Task) {
        val id = task._id ?: return
        lifecycleScope.launch {
            try {
                RetrofitClient.apiService.updateTask(
                    sessionManager.getBearerToken(),
                    id,
                    mapOf("isComplete" to !task.isComplete)
                )
                loadTasks()
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task", e)
                Toast.makeText(this@DashboardActivity, "Couldn't update task.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTask(task: Task) {
        val id = task._id ?: return
        lifecycleScope.launch {
            try {
                RetrofitClient.apiService.deleteTask(sessionManager.getBearerToken(), id)
                loadTasks()
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task", e)
                Toast.makeText(this@DashboardActivity, "Couldn't delete task.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
