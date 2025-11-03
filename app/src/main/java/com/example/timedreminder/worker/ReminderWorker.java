package com.example.timedreminder.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.timedreminder.NotificationHelper;
import com.example.timedreminder.ReminderPreferences;
import com.example.timedreminder.ReminderTimeUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReminderWorker extends Worker {

    private static final long SLOT_TOLERANCE_MINUTES = 30L;

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        if (!ReminderPreferences.isReminderEnabled(context)) {
            return Result.success();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalTime wakeTime = ReminderPreferences.getWakeTime(context);
        LocalTime sleepTime = ReminderPreferences.getSleepTime(context);

        if (!ReminderTimeUtils.isWithinActiveWindow(now.toLocalTime(), wakeTime, sleepTime)) {
            return Result.success();
        }

        LocalDateTime slot = ReminderTimeUtils.findMostRecentReminderSlot(now);
        if (!ReminderTimeUtils.isWithinTolerance(slot, now, SLOT_TOLERANCE_MINUTES)) {
            return Result.success();
        }

        String slotKey = ReminderTimeUtils.formatSlot(slot);
        if (slotKey.equals(ReminderPreferences.getLastTriggerSlot(context))) {
            return Result.success();
        }

        String content = ReminderPreferences.getNotificationContent(context);
        NotificationHelper.showReminderNotification(context, content);
        ReminderPreferences.setLastTriggerSlot(context, slotKey);

        return Result.success();
    }
}
