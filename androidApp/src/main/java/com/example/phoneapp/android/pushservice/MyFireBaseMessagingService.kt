package com.example.phoneapp.android.pushservice

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.content.edit

class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseService", "FCM token: $token")

        // Сохраняем токен
        val prefs = getSharedPreferences("push_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("fcm_token", token) }

        // Уведомляем о получении нового токена
        val intent = Intent("FCM_TOKEN_UPDATED")
        intent.putExtra("token", token)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        // Обновляем push-конфигурацию аккаунта если Core доступен
        try {
            val app = applicationContext as MyApplication
            val core = app.core
            val account = core.defaultAccount
            if (account != null) {
                val params = account.params.clone()

                // Обновляем contact parameters для push
                val contactParams = mutableMapOf<String, String>()
                contactParams["app-id"] = "firebase"
                contactParams["pn-provider"] = "firebase"
                contactParams["pn-param"] = token
                contactParams["pn-prid"] = token
                params.contactUriParameters = contactParams.toString()

                account.params = params
                Log.d("MyFirebaseService", "Push параметры обновлены для аккаунта")
            }
        } catch (e: Exception) {
            Log.e("MyFirebaseService", "Ошибка при обновлении push конфигурации", e)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseService", "FCM сообщение получено: ${remoteMessage.data}")

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            val payload = data["linphone-payload"] ?: data["payload"]
            Log.d("MyFirebaseService", "Payload: $payload")

            val caller = data["caller"] ?: data["from"] ?: "Unknown caller"

            NotificationUtils.showIncomingCallNotification(this, caller)

            try {
                val app = applicationContext as MyApplication
                val core = app.core

                if (payload != null) {
                    core.interpretUrl(payload, false)
                }
                Log.d("MyFirebaseService", "Push сообщение обработано Core")
            } catch (e: Exception) {
                Log.e("MyFirebaseService", "Ошибка при обработке push сообщения", e)
            }
        }
    }
}
