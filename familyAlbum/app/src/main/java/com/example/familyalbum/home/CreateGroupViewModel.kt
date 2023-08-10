package com.example.familyalbum.home

import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import com.google.firebase.firestore.FirebaseFirestore

class CreateGroupViewModel : ViewModel() {

    // Observable field for group name
    val groupName = ObservableField<String>()

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
                    // groupId를 이용하여 추가적인 작업을 할 수 있습니다.
                }
                .addOnFailureListener {
                    // 그룹 생성 실패 시 실행할 코드
                }
        }
    }
}
