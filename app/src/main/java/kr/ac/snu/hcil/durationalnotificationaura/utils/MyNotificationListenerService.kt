package kr.ac.snu.hcil.durationalnotificationaura.utils

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.*

class MyNotificationListenerService: NotificationListenerService() {
    companion object{
        const val ACTION = "kr.ac.snu.hcil.durationalnotificationaura.NOTIFICATION_LISTENER"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sendBroadcast(
            Intent(ACTION).apply{
                putExtra("event", "Initialized")
                putExtra("IDs", activeNotifications.map{it.id}.toIntArray())
                putExtra("packageNames", activeNotifications.map{it.packageName}.toTypedArray())
                putExtra("postTimes", activeNotifications.map{it.postTime}.toLongArray())
            }
        )
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
}