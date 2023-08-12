package com.example.familyalbum.timeTable

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide

import com.example.familyalbum.R

import com.example.familyalbum.databinding.FragmentTimeTableBinding
import com.example.familyalbum.task.Task
import com.example.familyalbum.task.TaskPlusActivity
import com.example.familyalbum.timeTable.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class TimeTableFragment : Fragment() {

    private lateinit var binding: FragmentTimeTableBinding
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private var firestore = FirebaseFirestore.getInstance()
    private lateinit var fragmentContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
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

        val bundle = arguments
        val startTime = bundle?.getString("startTime")
        val endTime = bundle?.getString("endTime")
        val week = bundle?.getString("week")
        val taskName = bundle?.getString("taskName")
        val taskPlace = bundle?.getString("taskPlace")

        if (startTime != null && endTime != null && taskPlace != null && taskName != null && week != null) {
            // 데이터를 활용하여 UI 업데이트 또는 다른 작업 수행
            // 예를 들면, 텍스트뷰에 데이터를 설정하는 코드:
            // view.findViewById<TextView>(R.id.textStartTime).text = startTime
            Log.i(startTime,startTime)
            Log.i(endTime,endTime)
            Log.i(taskPlace,taskPlace)
            Log.i(taskName,taskName)
            Log.i(week,week)

        } else {
            // 필요한 데이터가 없으면 오류 메시지 표시 또는 다른 처리 수행
            //Toast.makeText(context, "필요한 정보가 제공되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }


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

        myProfile()
        binding.imageView.setOnClickListener {
            showDialog()
        }

        binding.plusButton.setOnClickListener{
            val intent = Intent(activity, TaskPlusActivity::class.java)
            startActivity(intent)
        }

    }

    private fun myProfile() {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        var storage = FirebaseStorage.getInstance()

        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        uid?.let { userId ->
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userInfo = documentSnapshot.data
                        val name = userInfo?.get("name") as? String

                        val profileImageUrl = userInfo?.get("profileImageUrl") as? String
                        profileImageUrl?.let {
                            // Use Glide to load and display profile image
                            Glide.with(requireContext())
                                .load(profileImageUrl)
                                .placeholder(R.drawable.default_profile_image) // Placeholder image while loading
                                .error(R.drawable.default_profile_image) // Error image if loading fails
                                .circleCrop()
                                .into(binding.imageView)
                        }
                        binding.textView.text = "나의 시간표"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "데이터 처리 failed", exception)
                }
        }
    }
    fun showDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("우리가족 시간표 선택")
        alertDialogBuilder.setMessage("이미지를 클릭하셨습니다.")
        alertDialogBuilder.setPositiveButton("닫기", DialogInterface.OnClickListener { dialog, which ->
            // 확인 버튼 클릭 시 실행할 작업
            dialog.dismiss() // 다이얼로그 닫기
        })
        alertDialogBuilder.show()
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
                callback(User("", "", "", ""))
            }
        }.addOnFailureListener { exception ->
            // 에러 처리
            callback(User("", "", "", ""))
        }
    }

//    private fun loadTasksForCurrentUser(currentUserName: String, callback: (List<Task>) -> Unit) {
//        val taskRef = database.child("tasks").orderByChild("userName").equalTo(currentUserName)
//        taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val taskList = mutableListOf<Task>()
//                for (taskSnapshot in snapshot.children) {
//                    val task = taskSnapshot.getValue(Task::class.java)
//                    task?.let {
//                        taskList.add(it)
//                    }
//                }
//                callback(taskList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // 에러 처리
//                callback(emptyList())
//            }
//        })
//    }

    private fun loadTasksForCurrentUser(currentUserName: String, callback: (List<Task>) -> Unit) {
        val tasksCollection = firestore.collection("tasks")

        tasksCollection.whereEqualTo("userName", currentUserName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<Task>()
                for (documentSnapshot in querySnapshot.documents) {
                    val task = documentSnapshot.toObject(Task::class.java)
                    task?.let {
                        taskList.add(it)
                    }
                }
                callback(taskList)
            }
            .addOnFailureListener { exception ->
                // 에러 처리
                callback(emptyList())
            }
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

        // Table의 시간 범위 설정
        val startTime = 900
        val endTime = 2200
        val totalTimeRange = endTime - startTime
        val inflater = LayoutInflater.from(fragmentContext)


        for (task in taskList) {
            val inflater = LayoutInflater.from(fragmentContext)

            // Task의 시작시간과 종료시간을 가져오기
            val taskStartTime = task.startTime.toInt()
            val taskEndTime = task.endTime.toInt()

            // 시작시간과 종료시간을 전체 범위 내에서의 비율로 변환
            val ratio = (taskStartTime - startTime).toFloat() / totalTimeRange


            // 상단에서의 거리 계산
            val topDistance = (ratio * binding.root.height).toInt()

            // 높이 계산
            val taskHeight = ((taskEndTime - taskStartTime).toFloat() / totalTimeRange * binding.root.height).toInt()

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

                customLayout.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("스케줄 상세정보")
                    alertDialogBuilder.setMessage("${start.text},${end.text},${name.text},${place.text}")
                    alertDialogBuilder.setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        parentView.removeView(customLayout)
                        dialog.dismiss() // 다이얼로그 닫기
                    })
                    .setNegativeButton("수정") { dialog, _ ->

                        val intent = Intent(context, TaskPlusActivity::class.java)
                        intent.putExtra("key", "수정화면") // 정보 추가
                        startActivity(intent)
                        dialog.dismiss() // 다이얼로그 닫기
                    }
                    alertDialogBuilder.show()
                }
                parentView.addView(customLayout)  // 인플레이션 된 사용자 정의 레이아웃을 부모 뷰에 추가


            }
        }
    }
}