package com.example.timedreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.timedreminder.ReminderScheduler;
import com.example.timedreminder.ReminderTrigger;

public class ReminderAlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_TRIGGER_REMINDER = "com.example.timedreminder.action.TRIGGER_REMINDER";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (!ACTION_TRIGGER_REMINDER.equals(action)) {
            return;
        }

        String expectedSlot = intent.getStringExtra(ReminderTrigger.KEY_EXPECTED_SLOT);
        ReminderTrigger.execute(context, expectedSlot);
        ReminderScheduler.scheduleNext(context);
    }
}
