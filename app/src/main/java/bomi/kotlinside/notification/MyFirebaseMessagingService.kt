package bomi.kotlinside.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import bomi.kotlinside.R
import bomi.kotlinside.ui.MainActivity
import bomi.kotlinside.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
open class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "MyFirebaseMessagingService"
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e(TAG, "onNewToken :: $p0")
//        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
        Log.e(TAG, "onMessageReceived :: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }
//
//        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

            val msgBody = it.body
            val msgTitle = it.title
            val intent = Intent(baseContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val channelId : String = it.channelId ?:getString(R.string.default_notification_channel_id)
            val builder = NotificationCompat.Builder(baseContext, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(msgTitle)
                .setContentText(msgBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val notificationMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationMgr.createNotificationChannel(NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH))
            }
            notificationMgr.notify(0, builder.build())
        }
    }
}