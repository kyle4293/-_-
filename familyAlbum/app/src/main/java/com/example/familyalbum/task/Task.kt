package com.example.familyalbum.task

data class Task(
    val dayOfWeek: String = "",         //요일(월,화...)
    val endTime: String = "",            //Task 종료시간
    val place: String = "",             //장소
    val startTime: String = "",         //Task 시작시간
    val title: String = "",             //Task
    val userName: String = ""            //User Id
)
