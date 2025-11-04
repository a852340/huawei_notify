package com.example.timedreminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ReminderTrigger {

    public static final String KEY_EXPECTED_SLOT = "expected_slot";
    private static final long SLOT_TOLERANCE_MINUTES = 30L;

    private ReminderTrigger() {
    }

    public static void execute(@NonNull Context context, @Nullable String expectedSlotKey) {
        if (!ReminderPreferences.isReminderEnabled(context)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalTime wakeTime = ReminderPreferences.getWakeTime(context);
        LocalTime sleepTime = ReminderPreferences.getSleepTime(context);

        if (!ReminderTimeUtils.isWithinActiveWindow(now.toLocalTime(), wakeTime, sleepTime)) {
            return;
        }

        LocalDateTime slot = ReminderTimeUtils.parseSlot(expectedSlotKey);
        if (slot == null) {
            slot = ReminderTimeUtils.findMostRecentReminderSlot(now);
        }

        if (!ReminderTimeUtils.isWithinActiveWindow(slot.toLocalTime(), wakeTime, sleepTime)) {
            return;
        }

        if (!ReminderTimeUtils.isWithinTolerance(slot, now, SLOT_TOLERANCE_MINUTES)) {
            return;
        }

        String slotKey = ReminderTimeUtils.formatSlot(slot);
        if (slotKey.equals(ReminderPreferences.getLastTriggerSlot(context))) {
            return;
        }

        String content = ReminderPreferences.getNotificationContent(context);
        NotificationHelper.showReminderNotification(context, content);
        ReminderPreferences.setLastTriggerSlot(context, slotKey);
    }
}
