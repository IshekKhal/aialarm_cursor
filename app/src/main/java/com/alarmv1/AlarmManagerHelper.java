package com.alarmv1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmManagerHelper {
    private static AlarmManager alarmManager;
    private static Context context;
    
    public static void initialize(Context ctx) {
        context = ctx.getApplicationContext();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
    
    public static void setAlarm(Alarm alarm) {
        if (alarm == null || alarmManager == null) return;
        
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm_id", alarm.getId());
        intent.putExtra("alarm_hour", alarm.getHour());
        intent.putExtra("alarm_minute", alarm.getMinute());
        intent.putExtra("alarm_label", alarm.getLabel());
        intent.putExtra("alarm_vibrate", alarm.isVibrate());
        intent.putExtra("alarm_ringtone_index", alarm.getRingtoneIndex());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        long triggerTime = alarm.getNextAlarmTime();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        
        // For repeating alarms, we'll set individual alarms for each day
        // Note: Android's setRepeating is not exact, so we use setExact for each occurrence
        if (alarm.isRepeating()) {
            boolean[] repeatDays = alarm.getRepeatDays();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(java.util.Calendar.HOUR_OF_DAY, alarm.getHour());
            calendar.set(java.util.Calendar.MINUTE, alarm.getMinute());
            calendar.set(java.util.Calendar.SECOND, 0);
            calendar.set(java.util.Calendar.MILLISECOND, 0);
            
            // Calendar.DAY_OF_WEEK: Sunday=1, Monday=2, ..., Saturday=7
            // Our array: Sunday=0, Monday=1, ..., Saturday=6
            int currentDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
            int currentDayIndex = currentDayOfWeek - 1;
            
            for (int i = 0; i < 7; i++) {
                if (repeatDays[i]) {
                    int daysUntil = (i - currentDayIndex + 7) % 7;
                    if (daysUntil == 0 && calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                        daysUntil = 7; // Next week if time has passed today
                    }
                    
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, daysUntil);
                    long repeatTime = calendar.getTimeInMillis();
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, -daysUntil); // Reset
                    
                    Intent repeatIntent = new Intent(context, AlarmReceiver.class);
                    repeatIntent.putExtra("alarm_id", alarm.getId());
                    repeatIntent.putExtra("alarm_hour", alarm.getHour());
                    repeatIntent.putExtra("alarm_minute", alarm.getMinute());
                    repeatIntent.putExtra("alarm_label", alarm.getLabel());
                    repeatIntent.putExtra("alarm_vibrate", alarm.isVibrate());
                    repeatIntent.putExtra("alarm_ringtone_index", alarm.getRingtoneIndex());
                    
                    PendingIntent repeatPendingIntent = PendingIntent.getBroadcast(
                            context,
                            alarm.getId() * 100 + i,
                            repeatIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, repeatTime, repeatPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, repeatTime, repeatPendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, repeatTime, repeatPendingIntent);
                    }
                }
            }
        }
    }
    
    
    public static void cancelAlarm(Alarm alarm) {
        if (alarm == null || alarmManager == null) return;
        
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        
        // Cancel repeating alarms
        if (alarm.isRepeating()) {
            boolean[] repeatDays = alarm.getRepeatDays();
            for (int i = 0; i < 7; i++) {
                if (repeatDays[i]) {
                    Intent repeatIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent repeatPendingIntent = PendingIntent.getBroadcast(
                            context,
                            alarm.getId() * 100 + i,
                            repeatIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );
                    alarmManager.cancel(repeatPendingIntent);
                    repeatPendingIntent.cancel();
                }
            }
        }
    }
}

