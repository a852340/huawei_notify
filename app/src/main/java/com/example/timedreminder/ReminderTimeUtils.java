package com.example.timedreminder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class ReminderTimeUtils {

    private static final DateTimeFormatter SLOT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ReminderTimeUtils() {
    }

    public static boolean isWithinActiveWindow(LocalTime current, LocalTime wake, LocalTime sleep) {
        if (wake.equals(sleep)) {
            return true;
        }
        if (wake.isBefore(sleep)) {
            return !current.isBefore(wake) && current.isBefore(sleep);
        } else {
            return !current.isBefore(wake) || current.isBefore(sleep);
        }
    }

    public static LocalDateTime findNextReminderDateTime(LocalDateTime now, LocalTime wake, LocalTime sleep) {
        List<Integer> reminderMinutes = new ArrayList<>();
        reminderMinutes.add(0);
        reminderMinutes.add(55);

        LocalDateTime searchStart = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        for (int dayOffset = 0; dayOffset <= 1; dayOffset++) {
            LocalDateTime dayStart = now.plusDays(dayOffset).withHour(0).withMinute(0).withSecond(0).withNano(0);
            for (int hour = 0; hour < 24; hour++) {
                for (Integer minute : reminderMinutes) {
                    LocalDateTime candidate = dayStart.withHour(hour).withMinute(minute);
                    if (candidate.isBefore(searchStart)) {
                        continue;
                    }
                    if (isWithinActiveWindow(candidate.toLocalTime(), wake, sleep)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    public static LocalDateTime findMostRecentReminderSlot(LocalDateTime now) {
        LocalDateTime truncated = now.withSecond(0).withNano(0);
        if (truncated.getMinute() >= 55) {
            return truncated.withMinute(55);
        }
        return truncated.withMinute(0);
    }

    public static boolean isWithinTolerance(LocalDateTime slot, LocalDateTime now, long toleranceMinutes) {
        Duration duration = Duration.between(slot, now);
        return !duration.isNegative() && duration.compareTo(Duration.ofMinutes(toleranceMinutes)) <= 0;
    }

    public static String formatSlot(LocalDateTime slot) {
        return slot.truncatedTo(ChronoUnit.MINUTES).format(SLOT_FORMATTER);
    }

    public static LocalDateTime parseSlot(String slotKey) {
        if (slotKey == null || slotKey.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(slotKey, SLOT_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
