package com.example.todo.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo.R
import com.example.todo.database.TaskDatabase
import com.example.todo.database.TaskEntity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.recyclerview_single_task.view.*

class TaskAdapter(val context: Context, val taskContentList:List<TaskEntity>,private val listener: OnItemClickListener) :RecyclerView.Adapter<TaskAdapter.ViewHolderTask>(){
  // lateinit var taskAdapter:TaskAdapter//

    class ViewHolderTask(view: View): RecyclerView.ViewHolder(view) {
        val textTask: MaterialTextView =view.findViewById(R.id.textViewTask)
        //val delete: ShapeableImageView =view.findViewById(R.id.deleteTaskBtn)
        val llContent: MaterialCardView =view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolderTask {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_single_task,parent,false)
        return ViewHolderTask(view)
    }

    override fun getItemCount(): Int {
        return taskContentList.size
    }
    interface OnItemClickListener {//
    fun ondeleteClick(taskId:Int)//

    }

    override fun onBindViewHolder(holder: TaskAdapter.ViewHolderTask, position: Int) {
        val taskItemObject=taskContentList[position]
        holder.textTask.text =taskItemObject.taskContent

        holder.llContent.deleteTaskBtn.setOnClickListener(View.OnClickListener {
              val taskEntity=TaskEntity(taskItemObject.taskContent,1)
                val result = DBAsynTask(context, taskItemObject.taskId, 3).execute().get()//
                if (result) {

                    Toast.makeText(context, "Task Deleted ", Toast.LENGTH_SHORT).show()
                    notifyItemRemoved(position)
                    listener.ondeleteClick(taskItemObject.taskId)//

                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
        })

        holder.llContent.setOnClickListener(View.OnClickListener {
            Toast.makeText(context,"Hey , you clicked me !",Toast.LENGTH_LONG).show()
        })
    }

    class DBAsynTask(val context: Context, val taskEntity: TaskEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        val db= Room.databaseBuilder(context, TaskDatabase::class.java, "asks012-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant*/
            when (mode) {


                3 -> {
                    db.taskDao().deleteTask(taskEntity)
                    db.close()
                    return true
                }

                else->return false
            }
        }
    }
}