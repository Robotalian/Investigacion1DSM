package www.udb.edu.sv.todolistapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editTextTask: EditText
    private lateinit var editTextDueDate: EditText
    private lateinit var editTextCategory: EditText
    private lateinit var formContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
        loadTasks()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTasks)
        taskAdapter = TaskAdapter(tasks, ::editTask, ::deleteTask)
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        formContainer = findViewById(R.id.formContainer)
        editTextTask = findViewById(R.id.editTextTask)
        editTextDueDate = findViewById(R.id.editTextDueDate)
        editTextCategory = findViewById(R.id.editTextCategory)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        val buttonShowForm: Button = findViewById(R.id.buttonShowForm)

        editTextDueDate.setOnClickListener {
            showDatePicker()
        }

        buttonShowForm.setOnClickListener {
            formContainer.visibility = if (formContainer.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        buttonAdd.setOnClickListener {
            val title = editTextTask.text.toString()
            val dueDate = editTextDueDate.text.toString()
            val category = editTextCategory.text.toString()

            if (title.isNotEmpty() && dueDate.isNotEmpty() && category.isNotEmpty()) {
                if (isValidDate(dueDate)) {
                    val task = Task(title, dueDate, category)
                    tasks.add(task)
                    taskAdapter.notifyItemInserted(tasks.size - 1)
                    editTextTask.text.clear()
                    editTextDueDate.text.clear()
                    editTextCategory.text.clear()
                    formContainer.visibility = View.GONE
                    saveTasks()
                } else {
                    // Muestra un mensaje de error si la fecha es inválida
                    Toast.makeText(this, "La fecha debe ser posterior a la fecha actual", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Muestra un mensaje de error si algún campo está vacío
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
            editTextDueDate.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun isValidDate(dateString: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val selectedDate = sdf.parse(dateString) ?: return false
            val currentDate = Calendar.getInstance().time
            selectedDate.after(currentDate)
        } catch (e: Exception) {
            false
        }
    }

    private fun editTask(position: Int) {
        val task = tasks[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_task, null)

        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextDueDate = dialogView.findViewById<EditText>(R.id.editTextDueDate)
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextCategory)
        val checkBoxCompleted = dialogView.findViewById<CheckBox>(R.id.checkBoxCompleted)

        // Configura los campos con los valores actuales
        editTextTitle.setText(task.title)
        editTextDueDate.setText(task.dueDate)
        editTextCategory.setText(task.category)
        checkBoxCompleted.isChecked = task.isCompleted

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Editar Tarea")
            .setView(dialogView)
            .setPositiveButton("Guardar", null) // Inicialmente sin listener
            .setNegativeButton("Cancelar", null)
            .create()

        alertDialog.setOnShowListener {
            val buttonSave = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            buttonSave.setOnClickListener {
                val newTitle = editTextTitle.text.toString()
                val newDueDate = editTextDueDate.text.toString()
                val newCategory = editTextCategory.text.toString()
                val isCompleted = checkBoxCompleted.isChecked

                if (newTitle.isNotEmpty() && newDueDate.isNotEmpty() && newCategory.isNotEmpty()) {
                    if (isValidDate(newDueDate)) {
                        task.title = newTitle
                        task.dueDate = newDueDate
                        task.category = newCategory
                        task.isCompleted = isCompleted
                        taskAdapter.notifyItemChanged(position)
                        saveTasks()
                        alertDialog.dismiss() // Cerrar el diálogo después de guardar
                    } else {
                        Toast.makeText(this, "La fecha debe ser posterior a la fecha actual", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }


    private fun deleteTask(position: Int) {
        tasks.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        saveTasks()
    }

    private fun saveTasks() {
        val editor = sharedPreferences.edit()
        val tasksString = tasks.joinToString(separator = "|") { "${it.title}||${it.dueDate}||${it.category}||${it.isCompleted}" }
        editor.putString("tasks_list", tasksString)
        editor.apply()
    }

    private fun loadTasks() {
        val tasksString = sharedPreferences.getString("tasks_list", "")
        if (!tasksString.isNullOrEmpty()) {
            val taskStrings = tasksString.split("|")
            for (i in taskStrings.indices step 4) {
                val title = taskStrings[i]
                val dueDate = taskStrings[i + 1]
                val category = taskStrings[i + 2]
                val isCompleted = taskStrings[i + 3].toBoolean()
                tasks.add(Task(title, dueDate, category, isCompleted))
            }
        }
    }
}
