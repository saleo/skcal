package com.simplemobiletools.calendar.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.simplemobiletools.calendar.extensions.postGroupedNotify
import com.simplemobiletools.calendar.extensions.updateListWidget
import com.simplemobiletools.calendar.helpers.NOTIFICATION_CONTENT
import com.simplemobiletools.calendar.helpers.NOTIFICATION_ID
import com.simplemobiletools.calendar.helpers.NOTIFICATION_TITLE
import com.simplemobiletools.calendar.helpers.NOTIFICATION_TS

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Simple Calendar")
        wakelock.acquire(5000)

        context.updateListWidget()
//        val id = intent.getIntExtra(EVENT_ID, -1)
//        if (id == -1) {
//            return
//        }
//
//        val event = context.dbHelper.getEventWithId(id)
//        if (event == null || event.getReminders().isEmpty()) {
//            return
//        }

        val ntfId = intent.getIntExtra(NOTIFICATION_ID, -1)
        val ntfTitle = intent.getStringExtra(NOTIFICATION_TITLE)
        val ntfContent = intent.getStringExtra(NOTIFICATION_CONTENT)
        val ntfTS=intent.getLongExtra(NOTIFICATION_TS,0)
        context.postGroupedNotify(ntfId,ntfTitle,ntfContent,ntfTS)
//        if (!event.ignoreEventOccurrences.contains(Formatter.getDayCodeFromTS(event.startTS).toInt())) {
//            context.postGroupedNotify(event,myId,myTitle,myContent)
//        }
//        context.scheduleAllEvents()
    }
}
