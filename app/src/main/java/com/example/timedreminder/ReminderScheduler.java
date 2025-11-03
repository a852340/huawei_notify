package com.example.timedreminder;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.timedreminder.worker.ReminderWorker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public final class ReminderScheduler {

    private static final String UNIQUE_WORK_NAME = "timed_reminder_worker";
    private static final String WORK_TAG = "timed_reminder_worker_tag";

    private ReminderScheduler() {
    }

    public static void schedule(Context context) {
        enqueueNextReminder(context, ExistingWorkPolicy.REPLACE);
    }

    public static void cancel(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME);
    }

    public static void updateSchedulerState(Context context, boolean enabled) {
        if (enabled) {
            schedule(context);
        } else {
            cancel(context);
        }
    }

    public static void scheduleNext(Context context) {
        enqueueNextReminder(context, ExistingWorkPolicy.APPEND_OR_REPLACE);
    }

    private static void enqueueNextReminder(Context context, ExistingWorkPolicy policy) {
        if (!ReminderPreferences.isReminderEnabled(context)) {
            return;
        }

        LocalTime wakeTime = ReminderPreferences.getWakeTime(context);
        LocalTime sleepTime = ReminderPreferences.getSleepTime(context);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReminder = ReminderTimeUtils.findNextReminderDateTime(now, wakeTime, sleepTime);
        if (nextReminder == null) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME);
            return;
        }

        long delayMillis = Math.max(0L, Duration.between(now, nextReminder).toMillis());

        Data inputData = new Data.Builder()
                .putString(ReminderWorker.KEY_EXPECTED_SLOT, ReminderTimeUtils.formatSlot(nextReminder))
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(WORK_TAG)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_WORK_NAME, policy, workRequest);
    }
}
