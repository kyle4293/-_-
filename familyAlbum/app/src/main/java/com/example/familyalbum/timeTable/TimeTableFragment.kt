package com.example.familyalbum

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.familyalbum.databinding.FragmentTimeTableBinding
import com.example.familyalbum.task.Task
import com.example.familyalbum.timeTable.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class TimeTableFragment : Fragment() {

    private lateinit var binding: FragmentTimeTableBinding
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()


        // 더미 데이터 생성 및 저장
//        val dummyTasks = listOf(
//            Task("Beomjun Kim", "과제1", "강의실A", "Mon", "1000", "1200"),
//            Task("Beomjun Kim", "미팅", "회의실B", "Tue", "1400", "1600"),
//            Task("Beomjun Kim", "운동", "체육관", "Wed", "1700", "1800")
//        )
//
//        for (task in dummyTasks) {
//            val key = database.child("tasks").push().key
//            key?.let {
//                database.child("tasks").child(key).setValue(task)
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val currentUserId = user.uid

            loadCurrentUser(currentUserId) { loadedUser ->
                loadTasksForCurrentUser(loadedUser.name) { taskList ->
                    //Task 모델들을 이용하여 시간표에 표시하는 로직
                    schedule(taskList)
                }
            }
        }
    }

    private fun loadCurrentUser(userId: String, callback: (User) -> Unit) {
        val userRef = firestore.collection("users").document(userId)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val loadedUser = documentSnapshot.toObject(User::class.java)
                loadedUser?.let {
                    callback(it)
                }
            } else {
                // 사용자 정보가 없을 경우에 대한 처리
                callback(User("", ""))
            }
        }.addOnFailureListener { exception ->
            // 에러 처리
            callback(User("", ""))
        }
    }

    private fun loadTasksForCurrentUser(currentUserName: String, callback: (List<Task>) -> Unit) {
        val taskRef = database.child("tasks").orderByChild("userName").equalTo(currentUserName)
        taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        taskList.add(it)
                    }
                }
                callback(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
                callback(emptyList())
            }
        })
    }

    private fun getDayIndex(dayOfWeek: String): Int {
        return when (dayOfWeek) {
            "mon" -> 0
            "tue" -> 1
            "wed" -> 2
            "thu" -> 3
            "fri" -> 4
            "sat" -> 5
            "sun" -> 6
            else -> -1 // 예외 처리
        }
    }

    private fun schedule(taskList: List<Task>) {
        val daysOfWeek = listOf(binding.monView, binding.tueView, binding.wedView, binding.thuView, binding.friView, binding.satView, binding.sunView) // 요일별 레이아웃 View

        for (task in taskList) {
            val inflater = LayoutInflater.from(context)

            // Task의 시작시간과 종료시간을 가져오기
            val taskStartTime = task.startTime.toInt()
            val taskEndTime = task.endTime.toInt()

            // Table의 시간 범위 설정
            val startTime = 900
            val endTime = 2200

            // 시작시간과 종료시간 사이에서의 비율 계산
            val ratio = (taskEndTime - taskStartTime).toFloat() / (endTime - startTime)

            // 상단에서의 거리 계산
            val topDistance = ((taskStartTime - startTime) * ratio).toInt()

            // 높이 계산
            val taskHeight = ((taskEndTime - taskStartTime) * ratio).toInt()

            val dayIndex = getDayIndex(task.dayOfWeek) // 요일 문자열을 인덱스로 변환
            if (dayIndex != -1) {
                val parentView = daysOfWeek[dayIndex] // 해당 요일의 레이아웃 가져오기
                // 사용자 정의 레이아웃을 인플레이션
                val customLayout: View = inflater.inflate(R.layout.schedule, parentView, false)

                // 원하는대로 레이아웃 매개변수를 설정
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    taskHeight
                ).apply {
                    topMargin = topDistance
                }
                customLayout.layoutParams = layoutParams

                // 시간표의 뷰 요소 설정
                val start: TextView = customLayout.findViewById(R.id.start)
                start.text = (taskStartTime / 100).toString()
                val end: TextView = customLayout.findViewById(R.id.end)
                end.text = (taskEndTime / 100).toString()
                val name: TextView = customLayout.findViewById(R.id.name)
                name.text = task.title
                val place: TextView = customLayout.findViewById(R.id.place)
                place.text = task.place

                parentView.addView(customLayout)  // 인플레이션 된 사용자 정의 레이아웃을 부모 뷰에 추가
            }
        }
    }
}