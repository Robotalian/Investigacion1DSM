package www.udb.edu.sv.todolistapp

data class Task(
    var title: String,
    var dueDate: String,
    var category: String,
    var isCompleted: Boolean = false
)
