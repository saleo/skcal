package net.euse.calendar.helpers

import com.simplemobiletools.commons.helpers.DAY_SECONDS
import net.euse.calendar.R

const val LOW_ALPHA = .3f
const val MEDIUM_ALPHA = .6f
const val STORED_LOCALLY_ONLY = 0

const val DAY_CODE = "day_code"
const val YEAR_LABEL = "year"
const val EVENT_ID = "event_id"
const val EVENT_OCCURRENCE_TS = "event_occurrence_ts"
const val NEW_EVENT_START_TS = "new_event_start_ts"
const val WEEK_START_TIMESTAMP = "week_start_timestamp"
const val NEW_EVENT_SET_HOUR_DURATION = "new_event_set_hour_duration"
const val WEEK_START_DATE_TIME = "week_start_date_time"
const val CALDAV = "Caldav"
const val OPEN_MONTH = "open_month"

const val MONTHLY_VIEW = 1
const val YEARLY_VIEW = 2
const val EVENTS_LIST_VIEW = 3
const val WEEKLY_VIEW = 4
const val DAILY_VIEW = 5
const val QINGXIN_VIEW=6
const val ABOUT_VIEW=7
const val ABOUT_INTRO_VIEW=8
const val ABOUT_CREDIT_VIEW=9
const val ABOUT_HEALTH_VIEW=10
const val ABOUT_LICENSE_VIEW=11
const val SETTINGS_VIEW=12

const val REMINDER_OFF = -1

const val DAY_SECONDS = 86400
const val WEEK = 604800
const val MONTH = 2592001    // exact value not taken into account, Joda is used for adding months and years
const val YEAR = 31536000

const val DAY_MINUTES = 24 * 60
const val WEEK_SECONDS = 7 * DAY_SECONDS

// Shared Preferences
const val USE_24_HOUR_FORMAT = "use_24_hour_format"
const val SUNDAY_FIRST = "sunday_first"
const val WEEK_NUMBERS = "week_numbers"
const val START_WEEKLY_AT = "start_weekly_at"
const val END_WEEKLY_AT = "end_weekly_at"
const val VIBRATE = "vibrate"
const val REMINDER_SOUND = "reminder_sound"
const val VIEW = "view"
const val REMINDER_MINUTES = "reminder_minutes"
const val REMINDER_MINUTES_2 = "reminder_minutes_2"
const val REMINDER_MINUTES_3 = "reminder_minutes_3"
const val DISPLAY_EVENT_TYPES = "display_event_types"
const val FONT_SIZE = "font_size"
const val CALDAV_SYNC = "caldav_sync"
const val CALDAV_SYNCED_CALENDAR_IDS = "caldav_synced_calendar_ids"
const val LAST_USED_CALDAV_CALENDAR = "last_used_caldav_calendar"
const val SNOOZE_DELAY = "snooze_delay"
const val DISPLAY_PAST_EVENTS = "display_past_events"
const val REPLACE_DESCRIPTION = "replace_description"
const val USE_SAME_SNOOZE = "use_same_snooze"
const val REMINDER_UNIFIED_TIME = "reminder_unified_time"
const val REMINDER_TIME_DOWNLOADIMPORT = "reminder_downloadimport_time"
const val NOTIFICATON_IDS="notification_ids"
const val HTTPRESONPONSECACHE_INSTALLED="httpResonponseCache_Installed"

const val LAST_SUCCESSFUL_DATA_IMPORT_TIME="last_successful_data_import_time"
const val LAST_LAUNCH_TIME="last_launch_time"
const val MAX_REDIRECTS=5
const val USER_AGENT="SKCAL"

val letterIDs = intArrayOf(R.string.sunday_letter, R.string.monday_letter, R.string.tuesday_letter, R.string.wednesday_letter,
        R.string.thursday_letter, R.string.friday_letter, R.string.saturday_letter)

// repeat_rule for weekly repetition
const val MONDAY = 1
const val TUESDAY = 2
const val WEDNESDAY = 4
const val THURSDAY = 8
const val FRIDAY = 16
const val SATURDAY = 32
const val SUNDAY = 64
const val EVERY_DAY = 127

// repeat_rule for monthly repetition
const val REPEAT_MONTH_SAME_DAY = 1                   // ie 25th every month
const val REPEAT_MONTH_ORDER_WEEKDAY_USE_LAST = 2     // ie every xth sunday. 4th if a month has 4 sundays, 5th if 5
const val REPEAT_MONTH_LAST_DAY = 3                   // ie every last day of the month
const val REPEAT_MONTH_ORDER_WEEKDAY = 4              // ie every 4th sunday, even if a month has 4 sundays only (will stay 4th even at months with 5)

// special event flags
const val FLAG_ALL_DAY = 1

// constants related to ICS file exporting / importing
const val BEGIN_CALENDAR = "BEGIN:VCALENDAR"
const val END_CALENDAR = "END:VCALENDAR"
const val CALENDAR_PRODID = "PRODID:-//Simple Mobile Tools//NONSGML Event Calendar//EN"
const val CALENDAR_VERSION = "VERSION:2.0"
const val BEGIN_EVENT = "BEGIN:VEVENT"
const val END_EVENT = "END:VEVENT"
const val BEGIN_ALARM = "BEGIN:VALARM"
const val END_ALARM = "END:VALARM"
const val DTSTART = "DTSTART"
const val DTEND = "DTEND"
const val LAST_MODIFIED = "LAST-MODIFIED"
const val DURATION = "DURATION:"
const val SUMMARY = "SUMMARY"
const val DESCRIPTION = "DESCRIPTION:"
const val UID = "UID:"
const val ACTION = "ACTION:"
const val TRIGGER = "TRIGGER:"
const val RRULE = "RRULE:"
const val CATEGORIES = "CATEGORIES:"
const val STATUS = "STATUS:"
const val EXDATE = "EXDATE"
const val BYDAY = "BYDAY"
const val BYMONTHDAY = "BYMONTHDAY"
const val LOCATION = "LOCATION:"
const val SEQUENCE="SEQUENCE:"
// this tag isn't a standard ICS tag, but there's no official way of adding a category color in an ics file
const val CATEGORY_COLOR = "COLOR:"

const val DISPLAY = "DISPLAY"
const val FREQ = "FREQ"
const val UNTIL = "UNTIL"
const val COUNT = "COUNT"
const val INTERVAL = "INTERVAL"
const val CONFIRMED = "CONFIRMED"
const val VALUE = "VALUE"
const val DATE = "DATE"

const val DAILY = "DAILY"
const val WEEKLY = "WEEKLY"
const val MONTHLY = "MONTHLY"
const val YEARLY = "YEARLY"

const val MO = "MO"
const val TU = "TU"
const val WE = "WE"
const val TH = "TH"
const val FR = "FR"
const val SA = "SA"
const val SU = "SU"

// font sizes
const val FONT_SIZE_SMALL = 0
const val FONT_SIZE_MEDIUM = 1
const val FONT_SIZE_LARGE = 2

const val SOURCE_SIMPLE_CALENDAR = "simple-calendar"
const val SOURCE_IMPORTED_ICS = "imported-ics"
const val SOURCE_CONTACT_BIRTHDAY = "contact-birthday"
const val SOURCE_CONTACT_ANNIVERSARY = "contact-anniversary"
const val SOURCE_ALL="all"
const val SOURCE_CUSTOMIZE_ANNIVERSARY = "customize-anniversary"

const val REMINDER_SWITCH ="reminder_switch"
const val REMINDER_INITIAL_TS=72000//20:00
const val REMINDER_INITIAL_TS_PLUS_30MIN=72000+30*60
const val REMINDER_INITIAL_TS_PLUS_60MIN=72000+60*60//21:00
const val REMINDER_INITIAL_TS_PLUS_90MIN=72000+90*60
const val REMINDER_INITIAL_TS_PLUS_120MIN=72000+120*60 //22:00
const val REMINDER_INITIAL_TS_PLUS_150MIN=72000+150*60
const val REMINDER_INITIAL_TS_PLUS_180MIN=72000+180*60 //23:00
const val REMINDER_INITIAL_TS_PLUS_210MIN=72000+210*60

const val INTRO_TYPE="intro_type"
const val HEALTH_TITLE="health_title"
const val HEALTH_CONTENT="health_content"
const val HEALTH_CONTENT2="health_content2"

const val NOTIFICATION_ID="notification_id"
const val NOTIFICATION_TITLE="notification_title"
const val NOTIFICATION_CONTENT="notification_content"
const val NOTIFICATION_TS="notification_ts"
const val APP_TAG="SKCAL"
const val POSTPONE_TS=5*60*1000
const val TODAY_CODE = "today_code"

const val ANCESTOR =1
const val PARENTS= 2
const val RESPECTER= 3
const val SPOUSE= 4
const val YOURSELF= 5

const val BIRTH_DAY= 1
const val FORBIDDEN_DAY= 2
const val ANNIVERSARY_DAY= 3

const val PLACEHOLDER_8WHITESPACE="                "
const val YEARS_LIMIT_CUSTOMIZE_EVENT=2

const val LICHUN_INDEX=3
const val LIXIA_INDEX=9
const val LIQIU_INDEX=15
const val LIDONG_INDEX=21

const val CHUNFEN_INDEX=6
const val QIUFEN_INDEX=18
const val XIAZHI_INDEX=12
const val DONGZHI_INDEX=24

const val ZHI_RELEVANT_DAY=-1
const val FEN_RELEVANT_DAY=-2
const val LI_RELEVANT_DAY=-3

const val SHOW_PROGRESSBAR=1
const val DISMISS_PROGRESSBAR=2
const val UPDATE_PROGRESSBAR=3

const val SCHEDULE_ACTIVE=1
const val SCHEDULE_CANCEL=2
const val SCHEDULE_ACTIVE_AFTER_CANCEL=3

const val REPEAT_DOWNLOAD_IMPORT_RESULT="repeatDownloadImportResult"

const val  REPEAT_DOWNLOAD_IMPORT_SUCCESS="success"
const val  REPEAT_DOWNLOAD_IMPORT_FAIL="fail"
