package www.udb.edu.sv.todolistapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.taskText)
        val dueDateText: TextView = itemView.findViewById(R.id.dueDateText)
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val checkBoxCompleted: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.taskText.text = task.title
        holder.dueDateText.text = task.dueDate
        holder.categoryText.text = task.category
        holder.checkBoxCompleted.isChecked = task.isCompleted

        holder.checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
        }

        holder.buttonEdit.setOnClickListener {
            onEdit(position)
        }

        holder.buttonDelete.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = tasks.size
}
