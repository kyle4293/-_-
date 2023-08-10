package com.example.familyalbum.task

data class Task(
    val userName: String = "",            //User Id
    val title: String = "",             //Task
    val place: String = "",             //장소
    val dayOfWeek: String = "",         //요일(월,화...)
    val startTime: String = "",         //Task 시작시간
    val endTime: String = ""            //Task 종료시간
)
