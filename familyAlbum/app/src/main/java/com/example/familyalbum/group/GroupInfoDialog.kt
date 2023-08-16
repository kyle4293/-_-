package com.example.familyalbum.group

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.GroupInfoDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FieldValue
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

        return dialogBuilder.create()
    }

    private fun initLayout() {
        binding?.groupName?.text = group.groupName
        binding?.groupId?.text = group.groupId
        binding?.memberRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
        binding?.memberRecyclerview?.adapter = memberAdapter

        binding?.btnConfirm?.setOnClickListener {
            dismiss()
        }

        binding?.btnDeleteGroup?.setOnClickListener {
            deleteGroup(group.groupId, group.groupName)
        }
    }

    private fun deleteGroup(groupId: String?, groupName: String?) {
        if (groupId == null || groupName == null) {
            return
        }
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("groups").document(groupId)

        groupRef.delete()
            .addOnSuccessListener {
                // 그룹 문서 삭제 성공
                deleteGroupInfoFromUserGroups(groupId, groupName)
            }
            .addOnFailureListener { exception ->
                // 그룹 문서 삭제 실패
                Toast.makeText(requireContext(), "Failed to delete group $groupName", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteGroupInfoFromUserGroups(groupId: String, groupName: String) {
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 컬렉션에서 groupId와 groupName을 삭제하는 로직
        firestore.collection("users").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userRef = firestore.collection("users").document(document.id)

                    userRef.get()
                        .addOnSuccessListener { userDoc ->
                            val userGroups = userDoc.get("groups") as? List<Map<String, String>>
                            val updatedGroups = userGroups?.filter { groupMap ->
                                val groupGroupId = groupMap["groupId"]
                                val groupGroupName = groupMap["groupName"]
                                groupGroupId != groupId || groupGroupName != groupName
                            }

                            if (updatedGroups != userGroups) {
                                userRef.update("groups", updatedGroups)
                            }

                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리 로직
            }
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
                        val userImage = documentSnapshot.getString("profileImageUrl") ?: ""
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