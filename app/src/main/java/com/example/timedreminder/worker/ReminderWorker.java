package com.example.timedreminder.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.timedreminder.ReminderScheduler;
import com.example.timedreminder.ReminderTrigger;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        if (isStopped()) {
            return Result.success();
        }

        try {
            ReminderTrigger.execute(context, getInputData().getString(ReminderTrigger.KEY_EXPECTED_SLOT));
            return Result.success();
        } finally {
            if (!isStopped()) {
                ReminderScheduler.scheduleNext(context);
            }
        }
    }
}
