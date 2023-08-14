//package com.example.familyalbum
//
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import com.google.firebase.messaging.ktx.remoteMessage
//
//class FCMMessagingService : FirebaseMessagingService() {
//    override fun onNewToken(token: String) {
//        //새로운 token이 생성될때마다 호출되는 callback
//        super.onNewToken(token)
//    }
//
//    override fun onMessageReceived(remotemessage: RemoteMessage) {
//        super.onMessageReceived(remotemessage)
//        remoteMessage.takeIf { it.data.isNotEmpty() }?.apply {
//            //push를 전달받으면 동작해야할 함수를 호출
//            //Ex) Notification 표시 등등..
//        }
//    }
//}