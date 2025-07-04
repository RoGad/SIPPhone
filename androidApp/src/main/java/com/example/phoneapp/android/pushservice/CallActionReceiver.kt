package com.example.phoneapp.android.pushservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.phoneapp.android.MainActivity

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val caller = intent.getStringExtra("caller")

        Log.d("CallActionReceiver", "Получено действие: $action от $caller")

        try {
            val app = context.applicationContext as MyApplication
            val core = app.core

            when (action) {
                "ANSWER_CALL" -> {
                    Log.d("CallActionReceiver", "Отвечаем на звонок")
                    core.currentCall?.accept()
                    NotificationUtils.cancelIncomingCallNotification(context)

                    // Открываем приложение
                    val mainIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("call_answered", true)
                    }
                    context.startActivity(mainIntent)
                }
                "DECLINE_CALL" -> {
                    Log.d("CallActionReceiver", "Отклоняем звонок")
                    core.currentCall?.terminate()
                    NotificationUtils.cancelIncomingCallNotification(context)
                }
            }
        } catch (e: Exception) {
            Log.e("CallActionReceiver", "Ошибка при обработке действия", e)
        }
    }
}
