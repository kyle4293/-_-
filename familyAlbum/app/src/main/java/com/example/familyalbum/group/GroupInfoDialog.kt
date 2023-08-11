package com.example.familyalbum.group

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.GroupInfoDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore


class GroupInfoDialog(private val group: Group) : DialogFragment(){

    private var binding: GroupInfoDialogBinding?= null
    private lateinit var memberAdapter: GroupMemberAdapter
    private lateinit var memberList: ArrayList<MemberInfo>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = GroupInfoDialogBinding.inflate(layoutInflater)
        memberList = ArrayList()
        memberAdapter = GroupMemberAdapter(memberList)


        loadAndDisplayGroupUsers(group.groupId) // 그룹원 정보 가져오기

        initLayout()

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(binding?.root)
            .setPositiveButton("확인", null)

        return dialogBuilder.create()

    }

    private fun initLayout() {
        binding?.groupId?.text = group.groupId
        binding?.memberRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
        binding?.memberRecyclerview?.adapter = memberAdapter
    }

    private fun loadAndDisplayGroupUsers(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("groups").document(groupId)

        groupRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val members = documentSnapshot.get("members") as? ArrayList<String>
                    members?.let {
                        fetchAndDisplayUserNames(it)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching group data: $exception")
            }
    }

    private fun fetchAndDisplayUserNames(userIds: List<String>) {
        val firestore = FirebaseFirestore.getInstance()

        for (userId in userIds) {
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("name")
                        val userImage = documentSnapshot.getString("image") ?: ""
                        if (!userName.isNullOrEmpty()) {
                            val memberInfo = MemberInfo(userName, userImage)
                            memberList.add(memberInfo) // 리스트에 MemberInfo 객체 추가
                            memberAdapter.notifyDataSetChanged() // 어댑터 갱신
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리 로직
                }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}