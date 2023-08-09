package com.example.familyalbum.task

data class Task(
    val userId: String?,            //User Id
    val title: String?,             //Task
    val place: String?,             //장소
    val dayOfWeek: String?,         //요일(월,화...)
    val startTime: String?,         //Task 시작시간
    val endTime: String?            //Task 종료시간
)

//data class User(
//    val userId: String? = null, // 사용자 ID (Firebase 데이터베이스에서 고유 식별자로 사용될 수 있음)
//    val userName: String? = null, // 사용자 이름
//    val subjects: List<Subject>? = null // 사용자의 과목 리스트 (개인 시간표)
//)
//유저 클래스에 사용자 시간표 넣어야함
