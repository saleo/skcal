package net.euse.calendar.helpers

import android.content.Context
import android.media.RingtoneManager
import android.text.format.DateFormat
import com.simplemobiletools.commons.helpers.BaseConfig
import net.euse.calendar.R
import java.util.*

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }


    var use24hourFormat: Boolean
        get() {
            val use24hourFormat = DateFormat.is24HourFormat(context)
            return prefs.getBoolean(USE_24_HOUR_FORMAT, use24hourFormat)
        }
        set(use24hourFormat) = prefs.edit().putBoolean(USE_24_HOUR_FORMAT, use24hourFormat).apply()

    var displayWeekNumbers: Boolean
        get() = prefs.getBoolean(WEEK_NUMBERS, false)
        set(displayWeekNumbers) = prefs.edit().putBoolean(WEEK_NUMBERS, displayWeekNumbers).apply()

    var startWeeklyAt: Int
        get() = prefs.getInt(START_WEEKLY_AT, 7)
        set(startWeeklyAt) = prefs.edit().putInt(START_WEEKLY_AT, startWeeklyAt).apply()

    var endWeeklyAt: Int
        get() = prefs.getInt(END_WEEKLY_AT, 23)
        set(endWeeklyAt) = prefs.edit().putInt(END_WEEKLY_AT, endWeeklyAt).apply()

    var vibrateOnReminder: Boolean
        get() = prefs.getBoolean(VIBRATE, false)
        set(vibrate) = prefs.edit().putBoolean(VIBRATE, vibrate).apply()

    var reminderSound: String
        get() = prefs.getString(REMINDER_SOUND, getDefaultNotificationSound())
        set(path) = prefs.edit().putString(REMINDER_SOUND, path).apply()

    var storedView: Int
        get() = prefs.getInt(VIEW, MONTHLY_VIEW)
        set(view) = prefs.edit().putInt(VIEW, view).apply()

    var defaultReminderMinutes: Int
        get() = prefs.getInt(REMINDER_MINUTES, 10)
        set(defaultReminderMinutes) = prefs.edit().putInt(REMINDER_MINUTES, defaultReminderMinutes).apply()

    var defaultReminderMinutes2: Int
        get() = prefs.getInt(REMINDER_MINUTES_2, REMINDER_OFF)
        set(defaultReminderMinutes2) = prefs.edit().putInt(REMINDER_MINUTES_2, defaultReminderMinutes2).apply()

    var defaultReminderMinutes3: Int
        get() = prefs.getInt(REMINDER_MINUTES_3, REMINDER_OFF)
        set(defaultReminderMinutes3) = prefs.edit().putInt(REMINDER_MINUTES_3, defaultReminderMinutes3).apply()

    var snoozeDelay: Int
        get() = prefs.getInt(SNOOZE_DELAY, 10)
        set(snoozeDelay) = prefs.edit().putInt(SNOOZE_DELAY, snoozeDelay).apply()

    var displayPastEvents: Int
        get() = prefs.getInt(DISPLAY_PAST_EVENTS, 0)
        set(displayPastEvents) = prefs.edit().putInt(DISPLAY_PAST_EVENTS, displayPastEvents).apply()

    var displayEventTypes: Set<String>
        get() = prefs.getStringSet(DISPLAY_EVENT_TYPES, HashSet<String>())
        set(displayEventTypes) = prefs.edit().remove(DISPLAY_EVENT_TYPES).putStringSet(DISPLAY_EVENT_TYPES, displayEventTypes).apply()

    var fontSize: Int
        get() = prefs.getInt(FONT_SIZE, FONT_SIZE_MEDIUM)
        set(size) = prefs.edit().putInt(FONT_SIZE, size).apply()

    var caldavSync: Boolean
        get() = prefs.getBoolean(CALDAV_SYNC, false)
        set(caldavSync) {
            //context.scheduleCalDAVSync(caldavSync)
            prefs.edit().putBoolean(CALDAV_SYNC, caldavSync).apply()
        }

    var caldavSyncedCalendarIDs: String
        get() = prefs.getString(CALDAV_SYNCED_CALENDAR_IDS, "")
        set(calendarIDs) = prefs.edit().putString(CALDAV_SYNCED_CALENDAR_IDS, calendarIDs).apply()

    var lastUsedCaldavCalendar: Int
        get() = prefs.getInt(LAST_USED_CALDAV_CALENDAR, getSyncedCalendarIdsAsList().first().toInt())
        set(calendarId) = prefs.edit().putInt(LAST_USED_CALDAV_CALENDAR, calendarId).apply()

    var replaceDescription: Boolean
        get() = prefs.getBoolean(REPLACE_DESCRIPTION, false)
        set(replaceDescription) = prefs.edit().putBoolean(REPLACE_DESCRIPTION, replaceDescription).apply()

    var reminderSwitch:Boolean
        get()=prefs.getBoolean(REMINDER_SWITCH,true)
        set(reminderSwitch)=prefs.edit().putBoolean(REMINDER_SWITCH,reminderSwitch).apply()

    var reminderTs:Int
        get()= prefs.getInt(REMINDER_UNIFIED_TIME, REMINDER_INITIAL_TS)//20:00 per day
        //get() = 11*3600+14*60
        set(unifiedReminderTs) = prefs.edit().putInt(REMINDER_UNIFIED_TIME,unifiedReminderTs).apply()

    var reminderTsForDownloadImport:Int
    //get()= prefs.getInt(REMINDER_TIME_DOWNLOADIMPORT, REMINDER_INITIAL_TS_PLUS_210MIN)//23:30 per day
        get() = 11*3600+2*60
        set(unifiedReminderTs) = prefs.edit().putInt(REMINDER_TIME_DOWNLOADIMPORT,unifiedReminderTs).apply()

    var ntfIDs: String
        get() = prefs.getString(NOTIFICATON_IDS, "")
        set(ntfIDs) = prefs.edit().putString(NOTIFICATON_IDS, ntfIDs).apply()

    var lastSuccessfulDataImportMilliSeconds:Long
        get()= prefs.getLong(LAST_SUCCESSFUL_DATA_IMPORT_TIME, -1)
    //get() = 11*3600+14*60
        set(lastSuccessfulDataImportMilliSeconds) = prefs.edit().putLong(LAST_SUCCESSFUL_DATA_IMPORT_TIME,lastSuccessfulDataImportMilliSeconds).apply()

    var lastLaunchMilliSeconds:Long
        get()= prefs.getLong(LAST_LAUNCH_TIME, -1)
    //get() = 11*3600+14*60
        set(lastLaunchMilliSeconds) = prefs.edit().putLong(LAST_LAUNCH_TIME,lastLaunchMilliSeconds).apply()


    fun getSyncedCalendarIdsAsList() = caldavSyncedCalendarIDs.split(",").filter { it.trim().isNotEmpty() } as ArrayList<String>

    fun getNtfIdsAsList() = ntfIDs.split(",").filter { it.trim().isNotEmpty() } as ArrayList<String>

    fun addDisplayEventType(type: String) {
        addDisplayEventTypes(HashSet<String>(Arrays.asList(type)))
    }

    private fun addDisplayEventTypes(types: Set<String>) {
        val currDisplayEventTypes = HashSet<String>(displayEventTypes)
        currDisplayEventTypes.addAll(types)
        displayEventTypes = currDisplayEventTypes
    }

    fun removeDisplayEventTypes(types: Set<String>) {
        val currDisplayEventTypes = HashSet<String>(displayEventTypes)
        currDisplayEventTypes.removeAll(types)
        displayEventTypes = currDisplayEventTypes
    }

    private fun getDefaultNotificationSound(): String {
        return try {
            RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION)?.toString() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getFontSize() = when (fontSize) {
        FONT_SIZE_SMALL -> getSmallFontSize()
        FONT_SIZE_MEDIUM -> getMediumFontSize()
        else -> getLargeFontSize()
    }

    private fun getSmallFontSize() = getMediumFontSize() - 3f
    private fun getMediumFontSize() = context.resources.getDimension(R.dimen.day_text_size) / context.resources.displayMetrics.density
    private fun getLargeFontSize() = getMediumFontSize() + 3f


}
