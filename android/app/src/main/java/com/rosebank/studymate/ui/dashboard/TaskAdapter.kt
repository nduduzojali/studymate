package com.rosebank.studymate.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rosebank.studymate.databinding.ItemTaskBinding
import com.rosebank.studymate.model.Task

class TaskAdapter(
    private val onToggleComplete: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<Task>()

    fun submitList(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.taskTitleText.text = task.title
            binding.taskModuleText.text = task.module?.ifBlank { task.priority ?: "" }
            binding.completeCheckBox.setOnCheckedChangeListener(null)
            binding.completeCheckBox.isChecked = task.isComplete

            binding.completeCheckBox.setOnCheckedChangeListener { _, _ ->
                onToggleComplete(task)
            }
            binding.deleteTaskButton.setOnClickListener {
                onDelete(task)
            }
        }
    }
}
