package com.example.timedreminder;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.timedreminder.worker.ReminderWorker;

import java.util.concurrent.TimeUnit;

public final class ReminderScheduler {

    private static final String UNIQUE_WORK_NAME = "timed_reminder_worker";

    private ReminderScheduler() {
    }

    public static void schedule(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(ReminderWorker.class, 15, TimeUnit.MINUTES)
                .setInitialDelay(ReminderTimeUtils.computeInitialDelayMillis(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, workRequest);
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
}
