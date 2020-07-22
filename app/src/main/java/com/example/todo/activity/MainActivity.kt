package com.example.todo.activity

import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo.R
import com.example.todo.adapter.TaskAdapter
import com.example.todo.database.TaskDatabase
import com.example.todo.database.TaskEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.add_new_task_dialog.*
import kotlinx.android.synthetic.main.add_new_task_dialog.view.*

class MainActivity : AppCompatActivity() {

    lateinit var floatingButtonAdd: FloatingActionButton
    lateinit var taskAdapter: TaskAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerViewSingleTask: RecyclerView
    lateinit var viewHolder: RecyclerView.ViewHolder

    var dbTaskList = arrayListOf<TaskEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        layoutManager = LinearLayoutManager(this)
        recyclerViewSingleTask = findViewById(R.id.taskLayout)

        init()//

        floatingButtonAdd=findViewById(R.id.floating_action_button)
        floatingButtonAdd.setOnClickListener(View.OnClickListener {
            openDialog()
        })

    }
    private fun init() {//
        dbTaskList = RetrieveTasks(this).execute().get() as ArrayList<TaskEntity>
        taskAdapter = TaskAdapter(this, dbTaskList.reversed(),object : TaskAdapter.OnItemClickListener {
            override fun ondeleteClick(taskId:Int) {
                init() }
        })//
        recyclerViewSingleTask.adapter = taskAdapter
        recyclerViewSingleTask.layoutManager = layoutManager
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerViewSingleTask)

    }

    fun openDialog()
    {
        val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.add_new_task_dialog, null)
        val dialogBuilder:MaterialAlertDialogBuilder= MaterialAlertDialogBuilder(this,R.style.RoundShapeTheme)
            .setView(view)
            .setPositiveButton(getString(R.string.add)) { dialog, _: Int ->

                val taskEntity=TaskEntity(view.textFieldNewTask.text.toString(),0)
                val result=MainActivity.DBAsynTask(this,taskEntity,2).execute().get()
                if(result)
                {
                    Toast.makeText(this,"Task Added",Toast.LENGTH_LONG).show()
                    taskAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                    init()//

                }
                else
                {
                    Toast.makeText(this,"Some Error Occurred!",Toast.LENGTH_LONG).show()
                }
                taskAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        dialogBuilder.create()
        dialogBuilder.show()
    }

    class RetrieveTasks(val context: Context) : AsyncTask<Void, Void, List<TaskEntity>>() {
        override fun doInBackground(vararg params: Void?): List<TaskEntity> {
            val db = Room.databaseBuilder(context, TaskDatabase::class.java, "asks012-db").build()

            return db.taskDao().getAllTasks()
        }
    }



    val itemTouchHelper=object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT)
    {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
            dbTaskList.removeAt(viewHolder.adapterPosition)
            taskAdapter.notifyItemRemoved(viewHolder.adapterPosition)
        }
    }


    class DBAsynTask(val context: Context, val taskEntity: TaskEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>() {

        val db= Room.databaseBuilder(context, TaskDatabase::class.java, "asks012-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {

                2 -> {
                    db.taskDao().insertTask(taskEntity)
                    db.close()
                    return true
                }

                else->return false
            }
        }
    }

}