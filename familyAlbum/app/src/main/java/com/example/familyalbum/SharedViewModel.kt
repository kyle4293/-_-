package com.example.familyalbum

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // 여러 프래그먼트 간에 공유할 데이터
    var currentGroupName: String = ""
}