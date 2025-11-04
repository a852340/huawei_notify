package com.example.timedreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1001;

    private TimePicker wakeTimePicker;
    private TimePicker sleepTimePicker;
    private TextInputEditText notificationContentEditText;
    private SwitchMaterial reminderSwitch;
    private TextView nextReminderText;
    private TextView permissionStatusText;

    private boolean internalSwitchChange = false;
    private boolean pendingEnableAfterPermission = false;
    private boolean pendingEnableAfterExactAlarm = false;
    private boolean pendingTestNotification = false;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHelper.ensureChannel(this);

        bindViews();
        populateFromPreferences();
        setupListeners();
        updatePermissionStatus();
        updateNextReminderStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pendingEnableAfterExactAlarm && canScheduleExactAlarms()) {
            pendingEnableAfterExactAlarm = false;
            ReminderPreferences.setReminderEnabled(this, true);
            setSwitchChecked(true);
            ReminderScheduler.schedule(this);
            maybePromptForBatteryOptimization();
        }
        updatePermissionStatus();
        updateNextReminderStatus();
    }

    private void bindViews() {
        wakeTimePicker = findViewById(R.id.time_picker_wake);
        sleepTimePicker = findViewById(R.id.time_picker_sleep);
        wakeTimePicker.setIs24HourView(true);
        sleepTimePicker.setIs24HourView(true);

        notificationContentEditText = findViewById(R.id.edit_notification_content);
        reminderSwitch = findViewById(R.id.switch_enable_reminder);
        nextReminderText = findViewById(R.id.text_next_reminder);
        permissionStatusText = findViewById(R.id.text_permission_status);
    }

    private void setupListeners() {
        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (internalSwitchChange) {
                return;
            }
            handleReminderToggle(isChecked);
        });

        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(v -> onSaveClicked());

        Button testButton = findViewById(R.id.button_test);
        testButton.setOnClickListener(v -> onTestNotification());

        Button batterySettingsButton = findViewById(R.id.button_battery_settings);
        batterySettingsButton.setOnClickListener(v -> openBatteryOptimizationSettings());
    }

    private void populateFromPreferences() {
        LocalTime wakeTime = ReminderPreferences.getWakeTime(this);
        LocalTime sleepTime = ReminderPreferences.getSleepTime(this);
        setTimePickerTime(wakeTimePicker, wakeTime);
        setTimePickerTime(sleepTimePicker, sleepTime);

        String content = ReminderPreferences.getNotificationContent(this);
        notificationContentEditText.setText(content);

        boolean enabled = ReminderPreferences.isReminderEnabled(this);
        setSwitchChecked(enabled);
        ReminderScheduler.updateSchedulerState(this, enabled);
    }

    private void handleReminderToggle(boolean enable) {
        if (enable) {
            if (!NotificationHelper.hasNotificationPermission(this)) {
                pendingEnableAfterPermission = true;
                setSwitchChecked(false);
                requestNotificationPermission();
                return;
            }
            if (!canScheduleExactAlarms()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingEnableAfterExactAlarm = true;
                    requestExactAlarmPermission();
                }
                setSwitchChecked(false);
                return;
            }
            pendingEnableAfterPermission = false;
            pendingEnableAfterExactAlarm = false;
            ReminderPreferences.setReminderEnabled(this, true);
            ReminderScheduler.schedule(this);
            maybePromptForBatteryOptimization();
        } else {
            pendingEnableAfterPermission = false;
            pendingEnableAfterExactAlarm = false;
            ReminderPreferences.setReminderEnabled(this, false);
            ReminderScheduler.cancel(this);
        }
        updatePermissionStatus();
        updateNextReminderStatus();
    }

    private void onSaveClicked() {
        LocalTime wakeTime = LocalTime.of(wakeTimePicker.getHour(), wakeTimePicker.getMinute());
        LocalTime sleepTime = LocalTime.of(sleepTimePicker.getHour(), sleepTimePicker.getMinute());
        String content = notificationContentEditText.getText() != null ? notificationContentEditText.getText().toString().trim() : "";
        if (TextUtils.isEmpty(content)) {
            content = getString(R.string.default_notification_content);
        }

        ReminderPreferences.setWakeTime(this, wakeTime);
        ReminderPreferences.setSleepTime(this, sleepTime);
        ReminderPreferences.setNotificationContent(this, content);
        ReminderPreferences.clearLastTriggerSlot(this);

        if (ReminderPreferences.isReminderEnabled(this)) {
            ReminderScheduler.schedule(this);
        }

        Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
        updateNextReminderStatus();
    }

    private void onTestNotification() {
        if (!NotificationHelper.hasNotificationPermission(this)) {
            pendingTestNotification = true;
            requestNotificationPermission();
            return;
        }
        pendingTestNotification = false;
        NotificationHelper.showReminderNotification(this, getNotificationContentForDisplay());
        Toast.makeText(this, R.string.toast_test_sent, Toast.LENGTH_SHORT).show();
    }

    private String getNotificationContentForDisplay() {
        String content = notificationContentEditText.getText() != null ? notificationContentEditText.getText().toString().trim() : "";
        if (TextUtils.isEmpty(content)) {
            content = getString(R.string.default_notification_content);
        }
        return content;
    }

    private void setTimePickerTime(TimePicker picker, LocalTime time) {
        picker.setHour(time.getHour());
        picker.setMinute(time.getMinute());
    }

    private void setSwitchChecked(boolean checked) {
        internalSwitchChange = true;
        reminderSwitch.setChecked(checked);
        internalSwitchChange = false;
    }

    private void updateNextReminderStatus() {
        if (!ReminderPreferences.isReminderEnabled(this)) {
            nextReminderText.setText(R.string.next_reminder_disabled);
            return;
        }

        LocalTime wake = ReminderPreferences.getWakeTime(this);
        LocalTime sleep = ReminderPreferences.getSleepTime(this);
        LocalDateTime nextReminder = ReminderTimeUtils.findNextReminderDateTime(LocalDateTime.now(), wake, sleep);
        if (nextReminder == null) {
            nextReminderText.setText(R.string.next_reminder_unknown);
        } else {
            String message = getString(R.string.next_reminder_prefix) + dateTimeFormatter.format(nextReminder);
            nextReminderText.setText(message);
        }
    }

    private void updatePermissionStatus() {
        String notificationStatus = NotificationHelper.hasNotificationPermission(this)
                ? getString(R.string.permission_status_on)
                : getString(R.string.permission_status_off);

        String batteryStatus = isIgnoringBatteryOptimizations()
                ? getString(R.string.permission_status_on)
                : getString(R.string.permission_status_off);

        String exactAlarmStatus = canScheduleExactAlarms()
                ? getString(R.string.permission_status_on)
                : getString(R.string.permission_status_off);

        permissionStatusText.setText(getString(R.string.permission_status_template, notificationStatus, batteryStatus, exactAlarmStatus));
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            showNotificationSettingsDialog();
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            showNotificationPermissionRationale();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
        }
    }

    private void showNotificationPermissionRationale() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_notification_title)
                .setMessage(R.string.notification_permission_rationale)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS))
                .setNegativeButton(R.string.dialog_negative, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showNotificationSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_notification_title)
                .setMessage(R.string.dialog_notification_message)
                .setPositiveButton(R.string.dialog_settings, (dialog, which) -> NotificationHelper.requestSystemNotificationSettings(this))
                .setNegativeButton(R.string.dialog_negative, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        return alarmManager != null && alarmManager.canScheduleExactAlarms();
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_exact_alarm_title)
                .setMessage(R.string.dialog_exact_alarm_message)
                .setPositiveButton(R.string.dialog_settings, (dialog, which) -> openExactAlarmSettings())
                .setNegativeButton(R.string.dialog_negative, (dialog, which) -> {
                    pendingEnableAfterExactAlarm = false;
                    dialog.dismiss();
                })
                .show();
    }

    private void openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setData(Uri.parse("package:" + getPackageName()));
        try {
            startActivity(intent);
        } catch (Exception exception) {
            Intent fallback = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            fallback.setData(Uri.parse("package:" + getPackageName()));
            try {
                startActivity(fallback);
            } catch (Exception ignored) {
                pendingEnableAfterExactAlarm = false;
                Toast.makeText(this, R.string.permission_status_off, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void maybePromptForBatteryOptimization() {
        if (isIgnoringBatteryOptimizations()) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_battery_title)
                .setMessage(R.string.dialog_battery_message)
                .setPositiveButton(R.string.dialog_positive, (dialog, which) -> openBatteryOptimizationSettings())
                .setNegativeButton(R.string.dialog_negative, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openBatteryOptimizationSettings() {
        if (isIgnoringBatteryOptimizations()) {
            Toast.makeText(this, R.string.permission_status_on, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
            } else {
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            }
            try {
                startActivity(intent);
            } catch (Exception ignored) {
                Toast.makeText(this, R.string.permission_status_off, Toast.LENGTH_SHORT).show();
            }
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean isIgnoringBatteryOptimizations() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return powerManager != null && powerManager.isIgnoringBatteryOptimizations(getPackageName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.ensureChannel(this);
                updatePermissionStatus();
                if (pendingEnableAfterPermission) {
                    pendingEnableAfterPermission = false;
                    if (!canScheduleExactAlarms()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            pendingEnableAfterExactAlarm = true;
                            requestExactAlarmPermission();
                        }
                        setSwitchChecked(false);
                    } else {
                        pendingEnableAfterExactAlarm = false;
                        ReminderPreferences.setReminderEnabled(this, true);
                        setSwitchChecked(true);
                        ReminderScheduler.schedule(this);
                        updateNextReminderStatus();
                        maybePromptForBatteryOptimization();
                    }
                }
                if (pendingTestNotification) {
                    pendingTestNotification = false;
                    NotificationHelper.showReminderNotification(this, getNotificationContentForDisplay());
                    Toast.makeText(this, R.string.toast_test_sent, Toast.LENGTH_SHORT).show();
                }
            } else {
                pendingEnableAfterPermission = false;
                pendingEnableAfterExactAlarm = false;
                pendingTestNotification = false;
                updatePermissionStatus();
                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
