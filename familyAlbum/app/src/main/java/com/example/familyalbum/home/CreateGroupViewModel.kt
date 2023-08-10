package com.example.familyalbum.home

import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class CreateGroupViewModel : ViewModel() {

    // Observable field for group name
    val groupName = ObservableField<String>()

    private val _groupCreationSuccess = MutableLiveData<Boolean>()
    val groupCreationSuccess: LiveData<Boolean>
        get() = _groupCreationSuccess

    fun onCreateGroupButtonClick() {
        val name = groupName.get()
        if (name != null && name.isNotBlank()) {
            // Firestore에 그룹 정보 저장 및 생성
            val firestore = FirebaseFirestore.getInstance()
            val newGroup = hashMapOf(
                "groupName" to name
                // 추가적인 그룹 정보 필드가 있다면 여기에 추가할 수 있습니다.
            )

            firestore.collection("groups")
                .add(newGroup)
                .addOnSuccessListener { documentReference ->
                    val groupId = documentReference.id
                    // 그룹 생성 성공 시 실행할 코드
                    _groupCreationSuccess.value = true

                }
                .addOnFailureListener {
                    // 그룹 생성 실패 시 실행할 코드
                    _groupCreationSuccess.value = false

                }
        }
    }
}
