package com.example.phoneapp.android.pushservice

import android.app.Application
import android.util.Log
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.Factory

class MyApplication : Application() {
    lateinit var core: Core

    override fun onCreate() {
        super.onCreate()

        try {
            Log.d("MyApplication", "Инициализация MyApplication")

            val factory = Factory.instance()
            val configPath = filesDir.resolve(".linphonerc").absolutePath

            core = factory.createCore(configPath, null, this)

            core.isPushNotificationEnabled = true
            core.isKeepAliveEnabled = true

            core.addListener(object : CoreListenerStub() {
                override fun onCallStateChanged(core: Core, call: Call, state: Call.State?, message: String) {
                    Log.d("MyApplication", "Call state changed: $state")

                    when (state) {
                        Call.State.IncomingReceived -> {
                            val caller = call.remoteAddress.asString()
                            Log.d("MyApplication", "Incoming call from: $caller")
                            try {
                                NotificationUtils.showIncomingCallNotification(this@MyApplication, caller)
                            } catch (e: Exception) {
                                Log.e("MyApplication", "Ошибка при показе уведомления", e)
                            }
                        }
                        Call.State.Connected, Call.State.StreamsRunning -> {
                            Log.d("MyApplication", "Call connected")
                            try {
                                NotificationUtils.cancelIncomingCallNotification(this@MyApplication)
                            } catch (e: Exception) {
                                Log.e("MyApplication", "Ошибка при отмене уведомления", e)
                            }
                        }
                        Call.State.End, Call.State.Error -> {
                            Log.d("MyApplication", "Call ended")
                            try {
                                NotificationUtils.cancelIncomingCallNotification(this@MyApplication)
                            } catch (e: Exception) {
                                Log.e("MyApplication", "Ошибка при отмене уведомления", e)
                            }
                        }
                        else -> {}
                    }
                }
            })

            core.start()
            Log.d("MyApplication", "Core started successfully")

        } catch (e: Exception) {
            Log.e("MyApplication", "Критическая ошибка при инициализации", e)
            throw e
        }
    }

    fun updatePushToken(token: String) {
        try {
            val account = core.defaultAccount
            if (account != null) {
                val params = account.params.clone()

                val contactParams = mutableMapOf<String, String>()
                contactParams["app-id"] = "firebase"
                contactParams["pn-provider"] = "firebase"
                contactParams["pn-param"] = token
                contactParams["pn-prid"] = token
                params.contactUriParameters = contactParams.toString()

                account.params = params
                Log.d("MyApplication", "Push token обновлен: $token")
            }
        } catch (e: Exception) {
            Log.e("MyApplication", "Ошибка при обновлении push токена", e)
        }
    }
}
