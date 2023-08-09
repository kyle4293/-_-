package com.example.familyalbum.task

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskManager(private val userId: String) {
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("timeTable")

    //Create
    fun createTask(task: Task, callback: (Boolean) -> Unit) {
        val taskRef = tasksRef.push()
        taskRef.setValue(task)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    //Read
    fun getTask(taskId: String, callback: (task: Task?) -> Unit) {
        val taskRef = tasksRef.child(taskId)

        taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val task = snapshot.getValue(Task::class.java)
                callback(task)
            }

            override fun onCancelled(error: DatabaseError) {
                //에러 처리
                callback(null)
                Log.e("TaskManager", "Error reading task: $error")
            }
        })
    }

    //Read TaskBoard
    fun getUserTasks(userId: String, callback: (List<Task>) -> Unit) {      //userId를 매개변수로 등록한 이유, 클래스에서 이미 userId 갖고있지만 다른 구성원 조회할 때 필요하다고 생각함.
        // db에서 해당 사용자의 Task정보를 읽어옴
        tasksRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = mutableListOf<Task>()
                for (childSnapshot in snapshot.children) {
                    val task = childSnapshot.getValue(Task::class.java)
                    task?.let { tasks.add(it) }
                }
                callback(tasks)
            }
            override fun onCancelled(error: DatabaseError) {
                // 실패 처리
                callback(emptyList())
                Log.e("TaskManager", "Error reading user tasks: $error")
            }
        })
    }

    //Update
    fun updateTask(taskId: String, updatedTask: Task) {
        val taskRef = tasksRef.child(taskId)
        taskRef.setValue(updatedTask)
    }

    //Delete
    fun deleteTask(taskId: String) {
        val taskRef = tasksRef.child(taskId)
        taskRef.removeValue()
    }
}
