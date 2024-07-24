package www.udb.edu.sv.todolistapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTasks)
        taskAdapter = TaskAdapter(tasks, ::editTask, ::deleteTask)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val editTextTask: EditText = findViewById(R.id.editTextTask)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)

        buttonAdd.setOnClickListener {
            val task = editTextTask.text.toString()
            if (task.isNotEmpty()) {
                tasks.add(task)
                taskAdapter.notifyItemInserted(tasks.size - 1)
                editTextTask.text.clear()
            }
        }
    }

    private fun editTask(position: Int) {
        val currentTask = tasks[position]
        val editText = EditText(this)
        editText.setText(currentTask)

        AlertDialog.Builder(this)
            .setTitle("Editar Tarea")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                tasks[position] = editText.text.toString()
                taskAdapter.notifyItemChanged(position)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteTask(position: Int) {
        tasks.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
    }
}