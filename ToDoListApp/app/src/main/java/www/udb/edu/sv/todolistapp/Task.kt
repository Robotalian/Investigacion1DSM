package www.udb.edu.sv.todolistapp

data class Task(
    var title: String,
    var dueDate: String, // Puedes usar un tipo de datos más complejo como LocalDate
    var category: String
)