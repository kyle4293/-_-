package com.example.familyalbum.group

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CreateGroupViewModel : ViewModel() {

    val groupName = MutableLiveData<String>()


    private val _groupCreationSuccess = MutableLiveData<Boolean>()
    val groupCreationSuccess: LiveData<Boolean>
        get() = _groupCreationSuccess

    private val _userGroups = MutableLiveData<List<Group>>()
    val userGroups: LiveData<List<Group>>
        get() = _userGroups

    private val _groupJoinResult = MutableLiveData<Boolean>()
    val groupJoinResult: LiveData<Boolean>
        get() = _groupJoinResult

    private val _groupJoinId = MutableLiveData<String>()
    val groupJoinId: LiveData<String>
        get() = _groupJoinId


    fun joinGroup(groupId: String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val firestore = FirebaseFirestore.getInstance()
        val userGroup = mapOf("groupId" to groupId)

        firestore.collection("groups")
            .document(groupId)
            .update("members", FieldValue.arrayUnion(currentUserUid))
            .addOnSuccessListener {
                firestore.collection("users")
                    .document(currentUserUid)
                    .update("groups", FieldValue.arrayUnion(userGroup))
                    .addOnSuccessListener {
                        _groupJoinResult.value = true
                    }
                    .addOnFailureListener {
                        _groupJoinResult.value = false
                    }
            }
            .addOnFailureListener {
                _groupJoinResult.value = false
            }
    }

    fun fetchUserGroups(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val userGroups = document.get("groups") as? List<Map<String, String>>
                val groups = userGroups?.map { groupMap ->
                    Group(groupMap["groupId"] ?: "", groupMap["groupName"] ?: "")
                }
                _userGroups.value = groups ?: emptyList()
            }
            .addOnFailureListener {
                _userGroups.value = emptyList()
            }
    }

    fun onCreateGroupButtonClick() {
        val name = groupName.value
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        if (name != null && name.isNotBlank()) {
            // Firestore에 그룹 정보 저장 및 생성
            val firestore = FirebaseFirestore.getInstance()
            val newGroup = hashMapOf(
                "groupName" to name,
                "members" to listOf(currentUserUid)
            )

            firestore.collection("groups")
                .add(newGroup)
                .addOnSuccessListener { documentReference ->
                    val groupId = documentReference.id
                    _groupJoinId.value = groupId
                    // 유저의 그룹 목록에 추가
                    val userGroup = mapOf("groupId" to groupId, "groupName" to name)
                    firestore.collection("users")
                        .document(currentUserUid)
                        .update("groups", FieldValue.arrayUnion(userGroup))
                        .addOnSuccessListener {
                            _groupCreationSuccess.value = true
                        }
                        .addOnFailureListener {
                            _groupCreationSuccess.value = false
                        }
                }
                .addOnFailureListener {
                    _groupCreationSuccess.value = false
                }

        }
    }
}
