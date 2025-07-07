package com.example.phoneapp.android.pushservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import org.linphone.core.Factory

class PushBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PushBroadcastReceiver", "Push received broadcast")

        Toast.makeText(context, "Incoming call push received", Toast.LENGTH_SHORT).show()

        val factory = Factory.instance()
        val configPath = context.filesDir.resolve(".linphonerc").absolutePath

        val core = factory.createCore(configPath, null, context)
        core.start()

        val callerName = "Incoming call"
        NotificationUtils.showIncomingCallNotification(context, callerName)
    }
}
