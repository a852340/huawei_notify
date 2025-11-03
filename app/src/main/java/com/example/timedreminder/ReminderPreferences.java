package com.example.timedreminder;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class ReminderPreferences {

    private static final String PREF_NAME = "reminder_prefs";
    private static final String KEY_WAKE_TIME = "wake_time";
    private static final String KEY_SLEEP_TIME = "sleep_time";
    private static final String KEY_NOTIFICATION_CONTENT = "notification_content";
    private static final String KEY_REMINDER_ENABLED = "reminder_enabled";
    private static final String KEY_LAST_TRIGGER_SLOT = "last_trigger_slot";

    private static final String DEFAULT_WAKE_TIME = "07:00";
    private static final String DEFAULT_SLEEP_TIME = "23:00";
    private static final String DEFAULT_NOTIFICATION_CONTENT = "定时提醒";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    private ReminderPreferences() {
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static LocalTime getWakeTime(Context context) {
        String value = getPreferences(context).getString(KEY_WAKE_TIME, DEFAULT_WAKE_TIME);
        return parseTime(value, DEFAULT_WAKE_TIME);
    }

    public static void setWakeTime(Context context, LocalTime time) {
        getPreferences(context).edit().putString(KEY_WAKE_TIME, format(time)).apply();
    }

    public static LocalTime getSleepTime(Context context) {
        String value = getPreferences(context).getString(KEY_SLEEP_TIME, DEFAULT_SLEEP_TIME);
        return parseTime(value, DEFAULT_SLEEP_TIME);
    }

    public static void setSleepTime(Context context, LocalTime time) {
        getPreferences(context).edit().putString(KEY_SLEEP_TIME, format(time)).apply();
    }

    public static String getNotificationContent(Context context) {
        return getPreferences(context).getString(KEY_NOTIFICATION_CONTENT, DEFAULT_NOTIFICATION_CONTENT);
    }

    public static void setNotificationContent(Context context, String content) {
        getPreferences(context).edit().putString(KEY_NOTIFICATION_CONTENT, content).apply();
    }

    public static boolean isReminderEnabled(Context context) {
        return getPreferences(context).getBoolean(KEY_REMINDER_ENABLED, false);
    }

    public static void setReminderEnabled(Context context, boolean enabled) {
        getPreferences(context).edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply();
    }

    public static String getLastTriggerSlot(Context context) {
        return getPreferences(context).getString(KEY_LAST_TRIGGER_SLOT, "");
    }

    public static void setLastTriggerSlot(Context context, String slotKey) {
        getPreferences(context).edit().putString(KEY_LAST_TRIGGER_SLOT, slotKey).apply();
    }

    public static void clearLastTriggerSlot(Context context) {
        getPreferences(context).edit().remove(KEY_LAST_TRIGGER_SLOT).apply();
    }

    private static LocalTime parseTime(String value, String fallback) {
        try {
            return LocalTime.parse(value, FORMATTER);
        } catch (DateTimeParseException | NullPointerException ignored) {
            return LocalTime.parse(fallback, FORMATTER);
        }
    }

    private static String format(LocalTime time) {
        return time.format(FORMATTER);
    }
}
