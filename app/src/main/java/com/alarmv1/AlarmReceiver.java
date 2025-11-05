package com.alarmv1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "alarm_channel";
    private static final int NOTIFICATION_ID = 1000;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarm_id", -1);
        int hour = intent.getIntExtra("alarm_hour", 0);
        int minute = intent.getIntExtra("alarm_minute", 0);
        String label = intent.getStringExtra("alarm_label");
        boolean vibrate = intent.getBooleanExtra("alarm_vibrate", true);
        int ringtoneIndex = intent.getIntExtra("alarm_ringtone_index", 0);
        
        // Create notification channel for Android 8.0+
        createNotificationChannel(context);
        
        // Create intent for AlarmRingingActivity
        Intent alarmIntent = new Intent(context, AlarmRingingActivity.class);
        alarmIntent.putExtra("alarm_id", alarmId);
        alarmIntent.putExtra("alarm_hour", hour);
        alarmIntent.putExtra("alarm_minute", minute);
        alarmIntent.putExtra("alarm_label", label);
        alarmIntent.putExtra("alarm_vibrate", vibrate);
        alarmIntent.putExtra("alarm_ringtone_index", ringtoneIndex);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Create PendingIntent for full-screen notification
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                alarmId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Build notification with full-screen intent
        String timeString = String.format("%02d:%02d", hour, minute);
        String notificationText = label != null && !label.isEmpty() ? label : "Alarm";
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Alarm: " + timeString)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        
        // Show notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID + alarmId, notificationBuilder.build());
        }
    }
    
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for alarm notifications");
            channel.enableVibration(true);
            channel.enableLights(true);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}

