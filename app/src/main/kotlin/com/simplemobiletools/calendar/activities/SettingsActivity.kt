package com.simplemobiletools.calendar.activities

import android.app.LoaderManager
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import cn.carbs.android.gregorianlunarcalendar.library.data.ChineseCalendar
import cn.carbs.android.gregorianlunarcalendar.library.view.GregorianLunarCalendarView
import com.simplemobiletools.calendar.BuildConfig
import com.simplemobiletools.calendar.R
import com.simplemobiletools.calendar.adapters.CustomizeEventsAdapter
import com.simplemobiletools.calendar.dialogs.CustomEventReminderDialog
import com.simplemobiletools.calendar.dialogs.CustomizeEventDialog
import com.simplemobiletools.calendar.dialogs.CustomizeLunarDialog
import com.simplemobiletools.calendar.dialogs.SelectCalendarsDialog
import com.simplemobiletools.calendar.extensions.*
import com.simplemobiletools.calendar.helpers.*
import com.simplemobiletools.calendar.helpers.Formatter
import com.simplemobiletools.calendar.models.Event
import com.simplemobiletools.calendar.models.EventType
import com.simplemobiletools.commons.dialogs.ConfirmationDialog
import com.simplemobiletools.commons.dialogs.RadioGroupDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CALENDAR
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_CALENDAR
import com.simplemobiletools.commons.interfaces.RefreshRecyclerViewListener
import com.simplemobiletools.commons.models.RadioItem
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.view.*
import kotlinx.android.synthetic.main.customize_event_item_settings_view.*
import kotlinx.android.synthetic.main.customize_event_item_settings_view.view.*
import org.joda.time.DateTime
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SettingsActivity : SimpleActivity() ,AdapterView.OnItemSelectedListener,View.OnClickListener {
    private val GET_RINGTONE_URI = 1
    private val COL_ID = "id"
    private val COL_START_TS = "start_ts"
    private val COL_TITLE = "title"
    private val COL_LUNAR = "lunar"
    private val COL_SOURCE = "source"
    private val COL_WHOMFOR = "whomfor"
    private val COL_WHATFOR = "whatfor"
    private val COL_PARENT_EVENT_ID = "event_parent_id"

    lateinit var res: Resources
    private var mStoredPrimaryColor = 0
    private var mReminderMinutes = 0

    private var mWhomFor=""
    private var mWhatFor=""
    lateinit var mHolder:RelativeLayout
    private var lastHash=0

    // These are the Contacts rows that we will retrieve
    val PROJECTION = arrayOf(COL_ID, "substr($COL_TITLE,1,instr($COL_TITLE,' ')-1) as whomfor",
            "substr($COL_TITLE,instr($COL_TITLE,' ')+1) as whatfor", COL_LUNAR)


    // This is the select criteria
    val SELECTION_CUSTOMIZED_EVENT_ORIGIN = "$COL_SOURCE=$SOURCE_CUSTOMIZE_ANNIVERSARY and $COL_PARENT_EVENT_ID=0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        tv_settings_customize_event_when.text= PLACEHOLDER_8WHITESPACE
        tv_settings_customize_event_when.underlineText()

        res = resources

        mStoredPrimaryColor = config.primaryColor
        setupCaldavSync()

    }

    override fun onResume() {
        super.onResume()
        setupCustomizeColors()
        setupUseEnglish()
        setupManageEventTypes()
        setupHourFormat()
        setupSundayFirst()
        setupAvoidWhatsNew()
        setupDeleteAllEvents()
        setupReplaceDescription()
        setupWeekNumbers()
        setupWeeklyStart()
        setupWeeklyEnd()
        setupDisplayPastEvents()
        setupFontSize()
        updateTextColors(ll_settings_holder)
        checkPrimaryColor()
        setupSectionColors()
        setupSnoozeDelay()
        setupUseSameSnooze()
        setupReminerGeneral()
        setupCustomizeEvent()

        setupBottomButtonBar(cl_settings_holder)
    }

    override fun onPause() {
        super.onPause()
        mStoredPrimaryColor = config.primaryColor
    }

    private fun checkPrimaryColor() {
        if (config.primaryColor != mStoredPrimaryColor) {
            dbHelper.getEventTypes {
                if (it.filter { it.caldavCalendarId == 0 }.size == 1) {
                    val eventType = it.first { it.caldavCalendarId == 0 }
                    eventType.color = config.primaryColor
                    dbHelper.updateEventType(eventType)
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent){
            acs_customize_event_whomfor -> {mWhomFor= parent?.getItemAtPosition(position).toString()}
            acs_customize_event_whatfor -> {mWhatFor= parent?.getItemAtPosition(position).toString()}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onClick(v: View?) {//for several coomponent:add button, when textview
        if (v == btn_customize_event_add ){
            if (mWhomFor.isEmpty()){
                toast(R.string.whomfor_empty)
                acs_customize_event_whomfor.requestFocus()
                return
            }
            if (mWhatFor.isEmpty()){
                toast(R.string.whatfor_empty)
                acs_customize_event_whatfor.requestFocus()
                return
            }
            if (tv_settings_customize_event_when.value.isEmpty()){
                toast(R.string.when_empty)
                tv_settings_customize_event_when.requestFocus()
                return
            }

            val title=mWhomFor+" "+mWhatFor
            dbHelper.getEventsWithSearchQuery(title) exithere@{searchedText, events ->
                if (!events.isEmpty()) {
                    var events1=events.filter { event -> event.lunar==tv_settings_customize_event_when.value }
                    if (!events1.isEmpty()) {
                        toast(R.string.customized_event_already_exist)
                        acs_customize_event_whomfor.requestFocus()
                        return@exithere
                    }

                    // lunar and when differs
                    events1=events.sortedBy { event -> event.id  }
                    ConfirmationDialog(this,R.string.replace_existTitle_orNot.toString(),negative = R.string.no){
                        val id=events1[0].id
                        val title=events1[0].title
                        val iDeleted=dbHelper.deleteEvents(arrayOf(id.toString()),true)
                        Log.d(APP_TAG,"customized event deleted both id=$id and those whose parentId is this $id,total count=$iDeleted")
                        addCustomizeEvent(title){
                            toast(R.string.customized_event_succeeded)
                            refreshItems()
                        }
                    }
                }
                else
                    addCustomizeEvent(title){
                        toast(R.string.customized_event_succeeded)
                        refreshItems()
                    }
            }

        }
        //no else since txt-when click was processed in setupCustomizeEvents

    }

    private fun refreshItems() {
        setupCustomizeEventList()
    }

    private fun setupSectionColors() {
        val adjustedPrimaryColor = getAdjustedPrimaryColor()
        arrayListOf(tv_settings_builtin_events_reminder_time_label, caldav_label, weekly_view_label, monthly_view_label, simple_event_list_label, simple_font_size_label).forEach {
            it.setTextColor(adjustedPrimaryColor)
        }
    }

    private fun setupCustomizeColors() {
        settings_customize_colors_holder.setOnClickListener {
            startCustomizationActivity()
        }
    }

    private fun setupUseEnglish() {
        settings_use_english_holder.beVisibleIf(config.wasUseEnglishToggled || Locale.getDefault().language != "en")
        settings_use_english.isChecked = config.useEnglish
        settings_use_english_holder.setOnClickListener {
            settings_use_english.toggle()
            config.useEnglish = settings_use_english.isChecked
            useEnglishToggled()
        }
    }

    private fun setupManageEventTypes() {
        settings_manage_event_types_holder.setOnClickListener {
            startActivity(Intent(this, ManageEventTypesActivity::class.java))
        }
    }

    private fun setupHourFormat() {
        settings_hour_format.isChecked = config.use24hourFormat
        settings_hour_format_holder.setOnClickListener {
            settings_hour_format.toggle()
            config.use24hourFormat = settings_hour_format.isChecked
        }
    }

    private fun setupCaldavSync() {
        settings_caldav_sync.isChecked = config.caldavSync
        settings_caldav_sync_holder.setOnClickListener {
            if (config.caldavSync) {
                toggleCaldavSync(false)
            } else {
                handlePermission(PERMISSION_WRITE_CALENDAR) {
                    if (it) {
                        handlePermission(PERMISSION_READ_CALENDAR) {
                            if (it) {
                                toggleCaldavSync(true)
                            }
                        }
                    }
                }
            }
        }

        settings_manage_synced_calendars_holder.beVisibleIf(config.caldavSync)
        settings_manage_synced_calendars_holder.setOnClickListener {
            showCalendarPicker()
        }
    }

    private fun toggleCaldavSync(enable: Boolean) {
        if (enable) {
            showCalendarPicker()
        } else {
            settings_caldav_sync.isChecked = false
            config.caldavSync = false
            settings_manage_synced_calendars_holder.beGone()
            config.getSyncedCalendarIdsAsList().forEach {
                CalDAVHandler(applicationContext).deleteCalDAVCalendarEvents(it.toLong())
            }
            dbHelper.deleteEventTypesWithCalendarId(config.caldavSyncedCalendarIDs)
        }
    }

    private fun showCalendarPicker() {
        val oldCalendarIds = config.getSyncedCalendarIdsAsList()

        SelectCalendarsDialog(this) {
            val newCalendarIds = config.getSyncedCalendarIdsAsList()
            if (newCalendarIds.isEmpty() && !config.caldavSync) {
                return@SelectCalendarsDialog
            }

            settings_manage_synced_calendars_holder.beVisibleIf(newCalendarIds.isNotEmpty())
            settings_caldav_sync.isChecked = newCalendarIds.isNotEmpty()
            config.caldavSync = newCalendarIds.isNotEmpty()
            toast(R.string.syncing)

            Thread {
                if (newCalendarIds.isNotEmpty()) {
                    val existingEventTypeNames = dbHelper.fetchEventTypes().map { it.getDisplayTitle().toLowerCase() } as ArrayList<String>
                    getSyncedCalDAVCalendars().forEach {
                        val calendarTitle = it.getFullTitle()
                        if (!existingEventTypeNames.contains(calendarTitle.toLowerCase())) {
                            val eventType = EventType(0, it.displayName, it.color, it.id, it.displayName, it.accountName)
                            existingEventTypeNames.add(calendarTitle.toLowerCase())
                            dbHelper.insertEventType(eventType)
                        }
                    }
                    CalDAVHandler(applicationContext).refreshCalendars(this) {}
                }

                val removedCalendarIds = oldCalendarIds.filter { !newCalendarIds.contains(it) }
                removedCalendarIds.forEach {
                    CalDAVHandler(applicationContext).deleteCalDAVCalendarEvents(it.toLong())
                    dbHelper.getEventTypeWithCalDAVCalendarId(it.toInt())?.apply {
                        dbHelper.deleteEventTypes(arrayListOf(this), true) {}
                    }
                }
                dbHelper.deleteEventTypesWithCalendarId(TextUtils.join(",", removedCalendarIds))
                toast(R.string.synchronization_completed)
            }.start()
        }
    }

    private fun setupSundayFirst() {
        settings_sunday_first.isChecked = config.isSundayFirst
        settings_sunday_first_holder.setOnClickListener {
            settings_sunday_first.toggle()
            config.isSundayFirst = settings_sunday_first.isChecked
        }
    }

    private fun setupAvoidWhatsNew() {
        settings_avoid_whats_new.isChecked = config.avoidWhatsNew
        settings_avoid_whats_new_holder.setOnClickListener {
            settings_avoid_whats_new.toggle()
            config.avoidWhatsNew = settings_avoid_whats_new.isChecked
        }
    }

    private fun setupDeleteAllEvents() {
        settings_delete_all_events_holder.setOnClickListener {
            ConfirmationDialog(this, messageId = R.string.delete_all_events_confirmation) {
                Thread {
                    dbHelper.deleteAllEvents()
                }.start()
            }
        }
    }

    private fun setupReplaceDescription() {
        settings_replace_description.isChecked = config.replaceDescription
        settings_replace_description_holder.setOnClickListener {
            settings_replace_description.toggle()
            config.replaceDescription = settings_replace_description.isChecked
        }
    }

    private fun setupWeeklyStart() {
        settings_start_weekly_at.text = getHoursString(config.startWeeklyAt)
        settings_start_weekly_at_holder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            (0..24).mapTo(items) { RadioItem(it, getHoursString(it)) }

            RadioGroupDialog(this@SettingsActivity, items, config.startWeeklyAt) {
                if (it as Int >= config.endWeeklyAt) {
                    toast(R.string.day_end_before_start)
                } else {
                    config.startWeeklyAt = it
                    settings_start_weekly_at.text = getHoursString(it)
                }
            }
        }
    }

    private fun setupWeeklyEnd() {
        settings_end_weekly_at.text = getHoursString(config.endWeeklyAt)
        settings_end_weekly_at_holder.setOnClickListener {
            val items = ArrayList<RadioItem>()
            (0..24).mapTo(items) { RadioItem(it, getHoursString(it)) }

            RadioGroupDialog(this@SettingsActivity, items, config.endWeeklyAt) {
                if (it as Int <= config.startWeeklyAt) {
                    toast(R.string.day_end_before_start)
                } else {
                    config.endWeeklyAt = it
                    settings_end_weekly_at.text = getHoursString(it)
                }
            }
        }
    }

    private fun setupWeekNumbers() {
        settings_week_numbers.isChecked = config.displayWeekNumbers
        settings_week_numbers_holder.setOnClickListener {
            settings_week_numbers.toggle()
            config.displayWeekNumbers = settings_week_numbers.isChecked
        }
    }

    private fun setupReminderSound(isEnabled: Boolean = true) {
        tv_settings_reminder_sound.isEnabled = isEnabled

        val noRingtone = res.getString(R.string.no_ringtone_selected)
        if (config.reminderSound.isEmpty()) {
            tv_settings_reminder_sound.text = noRingtone
        } else {
            tv_settings_reminder_sound.text = RingtoneManager.getRingtone(this, Uri.parse(config.reminderSound))?.getTitle(this) ?: noRingtone
        }
        tv_settings_reminder_sound.setOnClickListener {
            Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, res.getString(R.string.reminder_sound_label))
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(config.reminderSound))

                if (resolveActivity(packageManager) != null)
                    startActivityForResult(this, GET_RINGTONE_URI)
                else {
                    toast(R.string.no_ringtone_picker)
                }
            }
        }
    }

    private fun setupVibrate(isEnabled: Boolean = true) {
        sc_settings_reminder_vibrate.isEnabled = isEnabled
        rl_settings_reminder_vibrate_holder.isEnabled = isEnabled
        if (isEnabled) {
            rl_settings_reminder_vibrate_holder.setOnClickListener {
                sc_settings_reminder_vibrate.toggle()
                config.vibrateOnReminder = sc_settings_reminder_vibrate.isChecked
            }
            sc_settings_reminder_vibrate.isChecked = config.vibrateOnReminder
        } else
            sc_settings_reminder_vibrate.isChecked = false
        config.vibrateOnReminder = sc_settings_reminder_vibrate.isChecked
    }

    private fun setupUseSameSnooze() {
        settings_use_same_snooze.isChecked = config.useSameSnooze
        settings_use_same_snooze_holder.setOnClickListener {
            settings_use_same_snooze.toggle()
            config.useSameSnooze = settings_use_same_snooze.isChecked
        }
    }

    private fun setupSnoozeDelay() {
        updateSnoozeText()
        settings_snooze_delay.setOnClickListener {
            showEventReminderDialog(config.snoozeDelay, true) {
                config.snoozeDelay = it
                updateSnoozeText()
            }
        }
    }


    private fun updateSnoozeText() {
        settings_snooze_delay.text = res.getQuantityString(R.plurals.by_minutes, config.snoozeDelay, config.snoozeDelay)
    }

    private fun getHoursString(hours: Int): String {
        return if (hours < 10) {
            "0$hours:00"
        } else {
            "$hours:00"
        }
    }

    private fun setupDisplayPastEvents() {
        var displayPastEvents = config.displayPastEvents
        updatePastEventsText(displayPastEvents)
        settings_display_past_events_holder.setOnClickListener {
            CustomEventReminderDialog(this, displayPastEvents) {
                displayPastEvents = it
                config.displayPastEvents = it
                updatePastEventsText(it)
            }
        }
    }

    private fun updatePastEventsText(displayPastEvents: Int) {
        settings_display_past_events.text = getDisplayPastEventsText(displayPastEvents)
    }

    private fun getDisplayPastEventsText(displayPastEvents: Int): String {
        return if (displayPastEvents == 0) {
            getString(R.string.never)
        } else {
            getFormattedMinutes(displayPastEvents, false)
        }
    }

    private fun setupFontSize() {
        settings_font_size.text = getFontSizeText()
        settings_font_size_holder.setOnClickListener {
            val items = arrayListOf(
                    RadioItem(FONT_SIZE_SMALL, res.getString(R.string.small)),
                    RadioItem(FONT_SIZE_MEDIUM, res.getString(R.string.medium)),
                    RadioItem(FONT_SIZE_LARGE, res.getString(R.string.large)))

            RadioGroupDialog(this@SettingsActivity, items, config.fontSize) {
                config.fontSize = it as Int
                settings_font_size.text = getFontSizeText()
                updateWidgets()
                updateListWidget()
            }
        }
    }

    private fun getFontSizeText() = getString(when (config.fontSize) {
        FONT_SIZE_SMALL -> R.string.small
        FONT_SIZE_MEDIUM -> R.string.medium
        else -> R.string.large
    })

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_RINGTONE_URI) {
                var uri = data?.getParcelableExtra<Parcelable>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)

                if (uri == null) {
                    config.reminderSound = ""
                } else {
                    try {
                        if ((uri as Uri).scheme == "file") {
                            uri = getFilePublicUri(File(uri.path), BuildConfig.APPLICATION_ID)
                        }
                        tv_settings_reminder_sound.text = RingtoneManager.getRingtone(this, uri)?.getTitle(this)
                        config.reminderSound = uri.toString()
                    } catch (e: Exception) {
                        showErrorToast(e)
                    }
                }
            }
        }
    }

    private fun setupReminderUnifiedMinute(isEnabled: Boolean = true) {

        tv_settings_builtin_events_reminder_unified_minute.isEnabled = isEnabled
        tv_settings_builtin_events_reminder_unified_minute.text = getFormattedMinutes(config.currentReminderMinutes)
        if (isEnabled)
            tv_settings_builtin_events_reminder_unified_minute.setOnClickListener { showReminderDialog() }
    }

    private fun setupReminderSwitch(isChecked: Boolean = true) {
        sc_settings_reminder_switch.isChecked = isChecked //no trigger clicklistener when enter here FIRST
//        settings_reminder_switch.textSize=config.fontSize.toFloat()
        setupReminderUnifiedMinute(isChecked)
        setupVibrate(isChecked)
        setupReminderSound(isChecked)

        rl_settings_reminder_switch_holder.setOnClickListener {
            sc_settings_reminder_switch.toggle()
            val reminderOnOff = sc_settings_reminder_switch.isChecked
            config.reminderSwitch = reminderOnOff
            setupReminderUnifiedMinute(reminderOnOff)
            setupVibrate(reminderOnOff)
            setupReminderSound(reminderOnOff)
            if (!reminderOnOff)
                Thread {
                    dbHelper.updateEventReminder(REMINDER_OFF)
                }.start()
        }
    }

    private fun setupReminerGeneral() {
        val reminderOnOff = config.reminderSwitch
        setupReminderSwitch(reminderOnOff)
    }

    private fun setupCustomizeEvent() {
        val whomfor_array = resources.getStringArray(R.array.whomfor)
        val whomfor_adapter = ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, whomfor_array)
        whomfor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acs_customize_event_whomfor.adapter = whomfor_adapter
        acs_customize_event_whomfor.onItemSelectedListener = this

        val whatfor_array = resources.getStringArray(R.array.whatfor)
        val whatfor_adapter = ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, whatfor_array)
        whatfor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        acs_customize_event_whatfor.adapter = whatfor_adapter
        acs_customize_event_whatfor.onItemSelectedListener = this

        tv_settings_customize_event_when.setOnClickListener {
            CustomizeLunarDialog(this, lunarDate=tv_settings_customize_event_when.value) { lunarDate, gregorianDate ->
                tv_settings_customize_event_when.text = lunarDate
                tv_settings_customize_event_when_gregorian.text = gregorianDate
            }
        }

        setupCustomizeEventList()

        btn_customize_event_add.setOnClickListener(this)
    }

    private fun setupCustomizeEventList() {
        val le=dbHelper.getCustomizedEvents()

        val newHash=le.hashCode()
        if (newHash == lastHash) {
            return
        }

        lastHash = newHash

        val ceAdapter = CustomizeEventsAdapter(this as SimpleActivity, java.util.ArrayList<Event>(le) , rv_customize_events,
                {any -> processRVItemClick(any as Event)},{any -> processRVItemLongClick(any as Event)})
        this.runOnUiThread { rv_customize_events.adapter=ceAdapter }


    }

    private fun showReminderDialog() {
        showEventReminderDialog(mReminderMinutes) {
            mReminderMinutes = it
            tv_settings_builtin_events_reminder_unified_minute.text = getFormattedMinutes(mReminderMinutes)
            config.currentReminderMinutes = mReminderMinutes
            Thread {
                dbHelper.updateEventReminder(mReminderMinutes)
                applicationContext.processEventRemindersNotification(dbHelper.getEventsToExport(false))
            }.start()
        }
    }


    private fun processRVItemClick(event:Event) {
        val title=event.title.split(" ")
        val whomfor = title[0]
        val whatfor = title[1]
        val lunarDate = event.lunar
        val id=event.id.toString()

        CustomizeEventDialog(this, whomfor, whatfor, lunarDate) { cb_whomfor, cb_whatfor, cb_lunarDate, cb_gregorianDate ->
            val title=cb_whomfor+" "+cb_whatfor
            val cb_startTs=Formatter.getDayStartTS(cb_gregorianDate)
            if (title == whomfor+" "+whatfor && lunarDate == cb_lunarDate) return@CustomizeEventDialog
            val iDeleted=dbHelper.deleteEvents(arrayOf(id),true)
            Log.d(APP_TAG,"customized event deleted both id=$id and those whose parentId is this $id,total count=$iDeleted")
            addCustomizeEvent(title,cb_startTs,cb_lunarDate){
                toast(R.string.customized_event_succeeded)
                refreshItems()
            }
        }
    }

    private fun processRVItemLongClick(event:Event):Boolean {
        val id=event.id.toString()
        val iDeleted=dbHelper.deleteEvents(arrayOf(id),true)
        toast(R.string.customized_event_delted)
        Log.d(APP_TAG,"customized event deleted both id=$id and those whose parentId is this $id,total count=$iDeleted")
        refreshItems()
        return true
    }

    private fun addCustomizeEvent(title:String,inStartTs:Int=0,inLunarDate:String="",callback:()->Unit) {
        var startTs=0;var endTs= 0; var parentId=0
        var iGregDayofMonth=0; var iGregMonth=0;var iGregYear=0
        var ggMonth="";var ggDayofMonth=""
        val lundarDate: String
        var idsToProcessNotification = ArrayList<String>()

        if (inStartTs == 0) {
            val gregorian = tv_settings_customize_event_when_gregorian.value

            startTs = Formatter.getDayStartTS(gregorian)
        } else {
            startTs = inStartTs
        }

        if (inLunarDate.isEmpty()) {
            lundarDate = tv_settings_customize_event_when.value
        } else {
            lundarDate = inLunarDate
        }

        var yearsToAdd = (getNowSeconds() - startTs) / YEAR

        if (yearsToAdd == 0) yearsToAdd++
        else if ((DateTime().dayOfYear) < (DateTime(startTs.toLong()).dayOfYear)) yearsToAdd++

        val reminderMinute1 = config.unifiedReminderTs / 60
        var lunarYear = lundarDate.substring(0, 4).toInt()
        val lunarMonth = lundarDate.substring(4, 6).toInt()
        val lunarDay = lundarDate.substring(6, 8).toInt()

        for (i in 0..2) {
            lunarYear += yearsToAdd
            val myCal = ChineseCalendar(true, lunarYear, lunarMonth, lunarDay)
            iGregYear = myCal.get(Calendar.YEAR)
            iGregMonth = myCal.get(Calendar.MONTH)
            iGregDayofMonth = myCal.get(Calendar.DAY_OF_MONTH)
            if (iGregMonth<10) ggMonth="0$iGregMonth" else ggMonth="$iGregMonth"
            if (iGregDayofMonth<10) ggDayofMonth="0$iGregDayofMonth" else ggDayofMonth="$iGregDayofMonth"

            startTs = Formatter.getDayStartTS("$iGregYear$ggMonth$ggDayofMonth")
            endTs = startTs + 1
            if (i==0) parentId=0
            val event = Event(0, startTs, endTs, title = title, reminder1Minutes = reminderMinute1, source = SOURCE_CUSTOMIZE_ANNIVERSARY,
                    color = Color.BLUE, lunar = lundarDate,parentId =parentId )
            dbHelper.insert(event, false) {
                Log.d(APP_TAG, "customized event inserted with id=$it,title=$title,startTs=$startTs")
                idsToProcessNotification.add(it.toString())
                if (i==0) parentId=it
            }
        }
        processEventRemindersNotification(idsToProcessNotification)
        callback()
    }
}
