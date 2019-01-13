package kr.ac.snu.hcil.durationalnotificationaura.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.content.Intent
import android.os.IBinder
import android.os.UserHandle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.*

class MyNotificationListenerService: NotificationListenerService() {
    companion object{
        const val ACTION = "kr.ac.snu.hcil.durationalnotificationaura.NOTIFICATION_LISTENER"
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        activeNotifications?.let{
            activeNotis ->
           sendBroadcast(
                    Intent(ACTION).apply{
                        putExtra("event", "Initialized")
                        putExtra("IDs", activeNotis.map{it.id}.toIntArray())
                        putExtra("packageNames", activeNotis.map{it.packageName}.toTypedArray())
                        putExtra("postTimes", activeNotis.map{it.postTime}.toLongArray())
                    }
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap) {
        super.onNotificationPosted(sbn)
        sendBroadcast(
            Intent(ACTION).apply{
                putExtra("event", "Posted")
                putExtra("ID", sbn?.id)
                putExtra("packageName", sbn?.packageName)
                putExtra("postTime", sbn?.postTime)
            }
        )
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap, reason:Int) {
        super.onNotificationRemoved(sbn)
        sendBroadcast(
            Intent(ACTION).apply{
                putExtra("event", "Removed")
                putExtra("ID", sbn?.id)
                putExtra("packageName", sbn?.packageName)
                putExtra("postTime", Calendar.getInstance().timeInMillis)
            }
        )
    }

    override fun onNotificationChannelGroupModified(
        pkg: String?,
        user: UserHandle?,
        group: NotificationChannelGroup?,
        modificationType: Int
    ) {
        super.onNotificationChannelGroupModified(pkg, user, group, modificationType)
    }

    override fun onNotificationChannelModified(
        pkg: String?,
        user: UserHandle?,
        channel: NotificationChannel?,
        modificationType: Int
    ) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType)
    }
}