package com.example.familyalbum.timeTable

import android.content.ContentValues
import android.content.ContentValues.TAG
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

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.familyalbum.MainActivity

import com.example.familyalbum.R

import com.example.familyalbum.databinding.FragmentTimeTableBinding
import com.example.familyalbum.group.Group
import com.example.familyalbum.group.TimeTableGroupInfoDialog
import com.example.familyalbum.task.Task
import com.example.familyalbum.task.TaskEditActivity
import com.example.familyalbum.task.TaskPlusActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class TimeTableFragment : Fragment(){

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
                loadUserProfile(loadedUser.name) { userImage ->
                    myProfile(loadedUser.name, userImage)
                }
            }
        }

        //프로필 이미지를 선택하면
        binding.imageView.setOnClickListener {
            showDialog()
        }

        //추가버튼을 누르면
        binding.plusButton.setOnClickListener{
            val intent = Intent(activity, TaskPlusActivity::class.java)
            startActivity(intent)
        }

    }

    private fun saveTaskToFirestore(task: Task) {
        val tasksCollection = firestore.collection("tasks")

        tasksCollection.add(task)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                // 저장 성공 시 수행할 작업 추가
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                // 저장 실패 시 수행할 작업 추가
            }
    }

    private fun myProfile(userName: String?, userImage: String?) {
        binding.textView.text = if (userName.isNullOrEmpty()) "나의 시간표" else "$userName 의 시간표"
        userImage?.let {
            // Use Glide to load and display profile image
            Glide.with(fragmentContext)
                .load(userImage)
                .placeholder(R.drawable.default_profile_image) // Placeholder image while loading
                .error(R.drawable.default_profile_image) // Error image if loading fails
                .circleCrop()
                .into(binding.imageView)
        }
    }


    fun showDialog() {

        //현재 그룹 이름
        val currentGroupID =  (activity as? MainActivity)?.sharedViewModel?.currentGroupID ?: ""
        val currentGroupName = (activity as? MainActivity)?.sharedViewModel?.currentGroupName ?: ""

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid
        var currentUserName: String? = null
        var currentUserImage: String? = null

        if (uid != null) {
            val userDocRef = firestore.collection("users").document(uid)
            userDocRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    currentUserName = documentSnapshot.getString("name")
                    currentUserImage = documentSnapshot.getString("profileImageURL")
                }
            }
        }

        val dialog = TimeTableGroupInfoDialog(Group(currentGroupID,currentGroupName))
        dialog.setDataListener { selectedUserName ->
            Log.i("selectedUserName",selectedUserName)

            //selectedUserName 이 현재 사용자의 이름과 같을 때
            //원래로직대로 myprofile, 나의 시간표들 출력 , 함수를 부르면 될듯
            if (selectedUserName == currentUserName) {
                if (uid != null) {
                    loadCurrentUser(uid) { loadedUser ->
                        loadTasksForCurrentUser(loadedUser.name) { taskList ->
                            schedule(taskList)
                        }
                    }
                    myProfile(currentUserName, currentUserImage)
                }
            } else {
                //selectedUserName 이 현재 사용자의 이름과 같지 않을 때
                //otherprofile, 그 유저이름의 시간표들 출력
                val query = firestore.collection("users").whereEqualTo("name", selectedUserName)
                query.get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val selectedUserDoc = querySnapshot.documents[0]
                        val selectedUserId = selectedUserDoc.id
                        loadCurrentUser(selectedUserId) { loadedUser ->
                            loadTasksForCurrentUser(loadedUser.name) { taskList ->
                                schedule(taskList)
                            }
                            loadUserProfile(loadedUser.name) { userImage ->
                                myProfile(selectedUserName, userImage)
                            }
                        }

                    }
                }
            }
        }
        val fragmentManager =
            (binding.root.context as? AppCompatActivity)?.supportFragmentManager
        fragmentManager?.let { manager ->
            dialog.show(manager, "GroupDialog")
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
                callback(User("", "", "", ""))
            }
        }.addOnFailureListener { exception ->
            // 에러 처리
            callback(User("", "", "", ""))
        }
    }

    private fun loadTasksForCurrentUser(currentUserName: String, callback: (List<Task>) -> Unit) {
        val tasksCollection = firestore.collection("tasks")

        tasksCollection.whereEqualTo("userName", currentUserName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val taskList = mutableListOf<Task>()
                taskList.clear()
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
    private fun loadUserProfile(userName: String, callback: (String) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("name", userName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val profileImageUrl = document.getString("profileImageUrl")
                    profileImageUrl?.let {
                        callback(it)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리 로직
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

        //기존 뷰 모두 제거
        for (parentView in daysOfWeek) {
            parentView.removeAllViews()
        }

        // Table의 시간 범위 설정
        val startTime = 900
        val endTime = 2200
        val totalTimeRange = endTime - startTime
        val inflater = LayoutInflater.from(fragmentContext)

        val currentUser = firebaseAuth.currentUser


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

                var currentUserId = ""
                if (currentUser != null) {
                    currentUserId = currentUser.uid
                }

                customLayout.setOnClickListener {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("스케줄 상세정보")
                    alertDialogBuilder.setMessage("${start.text},${end.text},${name.text},${place.text}")
                    var isHandlingClickEvent = false

                    alertDialogBuilder.setPositiveButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                        if (isHandlingClickEvent) {
                            return@OnClickListener // 이미 다른 작업을 처리 중인 경우 무시
                        }
                        isHandlingClickEvent = true // 작업 시작

                        //요기서 db도 삭제해야함니다
                        val tasksCollection = firestore.collection("tasks")
                        loadCurrentUser(currentUserId) { loadedUser ->
                            val currentUserName = loadedUser.name
                            tasksCollection.whereEqualTo("userName", currentUserName)
                                .whereEqualTo("title", task.title)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    for (documentSnapshot in querySnapshot.documents) {
                                        tasksCollection.document(documentSnapshot.id).delete()
                                            .addOnSuccessListener {
                                                // 성공적으로 삭제한 경우, 화면에서도 해당 뷰 제거
                                                parentView.removeView(customLayout)
                                                parentView.invalidate()
//                                                dialog.dismiss() // 다이얼로그 닫기
                                            }
                                            .addOnFailureListener { exception ->
                                                // 삭제 실패 시 처리
                                            }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // 조회 실패 시 처리
                                }
                        }
                        isHandlingClickEvent = false
                        dialog.dismiss()
                    })
                        .setNegativeButton("수정") { dialog, _ ->

                            if (isHandlingClickEvent) {
                                return@setNegativeButton // 이미 다른 작업을 처리 중인 경우 무시
                            }
                            isHandlingClickEvent = true // 작업 시작

                            dialog.dismiss() // 다이얼로그 닫기

                            val intent = Intent(context, TaskEditActivity::class.java)
                            intent.putExtra("startTime",task.startTime)
                            intent.putExtra("endTime",task.endTime)
                            intent.putExtra("title",task.title)
                            intent.putExtra("place",task.place)
                            intent.putExtra("dayOfWeek",task.dayOfWeek)
                            startActivity(intent)

                        }

                    alertDialogBuilder.show()
                }
                parentView.addView(customLayout)  // 인플레이션 된 사용자 정의 레이아웃을 부모 뷰에 추가
            }
        }
    }
}