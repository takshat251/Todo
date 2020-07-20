package com.example.todo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.material.textfield.TextInputEditText

@Entity(tableName = "tasks")
data class TaskEntity(
    @ColumnInfo(name="content") var taskContent: String,
     @PrimaryKey(autoGenerate = true) var taskId:Int=0
)
