package com.example.timedreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.timedreminder.receiver.ReminderAlarmReceiver;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public final class ReminderScheduler {

    private static final int REQUEST_CODE_REMINDER = 1001;

    private ReminderScheduler() {
    }

    public static void schedule(Context context) {
        enqueueNextReminder(context);
    }

    public static void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        PendingIntent pendingIntent = getExistingReminderPendingIntent(context);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static void updateSchedulerState(Context context, boolean enabled) {
        if (enabled) {
            schedule(context);
        } else {
            cancel(context);
        }
    }

    public static void scheduleNext(Context context) {
        enqueueNextReminder(context);
    }

    private static void enqueueNextReminder(Context context) {
        if (!ReminderPreferences.isReminderEnabled(context)) {
            cancel(context);
            return;
        }

        LocalTime wakeTime = ReminderPreferences.getWakeTime(context);
        LocalTime sleepTime = ReminderPreferences.getSleepTime(context);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReminder = ReminderTimeUtils.findNextReminderDateTime(now, wakeTime, sleepTime);
        if (nextReminder == null) {
            cancel(context);
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        long triggerAtMillis = nextReminder.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        PendingIntent pendingIntent = createReminderPendingIntent(context, ReminderTimeUtils.formatSlot(nextReminder));

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } catch (SecurityException ignored) {
            cancel(context);
        }
    }

    private static PendingIntent createReminderPendingIntent(Context context, String expectedSlot) {
        Intent intent = new Intent(context, ReminderAlarmReceiver.class)
                .setAction(ReminderAlarmReceiver.ACTION_TRIGGER_REMINDER)
                .putExtra(ReminderTrigger.KEY_EXPECTED_SLOT, expectedSlot);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getBroadcast(context, REQUEST_CODE_REMINDER, intent, flags);
    }

    private static PendingIntent getExistingReminderPendingIntent(Context context) {
        Intent intent = new Intent(context, ReminderAlarmReceiver.class)
                .setAction(ReminderAlarmReceiver.ACTION_TRIGGER_REMINDER);

        int flags = PendingIntent.FLAG_NO_CREATE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getBroadcast(context, REQUEST_CODE_REMINDER, intent, flags);
    }
}
