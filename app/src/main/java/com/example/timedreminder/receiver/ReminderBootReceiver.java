package com.example.timedreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.timedreminder.ReminderPreferences;
import com.example.timedreminder.ReminderScheduler;

public class ReminderBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            return;
        }
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {
            if (ReminderPreferences.isReminderEnabled(context)) {
                ReminderScheduler.schedule(context);
            }
        }
    }
}
