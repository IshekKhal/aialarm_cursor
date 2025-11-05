package com.alarmv1;

import java.util.Calendar;

public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private String label;
    private boolean enabled;
    private boolean[] repeatDays; // 7 days, Sunday = 0, Monday = 1, etc.
    private boolean vibrate;
    private int ringtoneIndex; // Index of ringtone (0-9) stored in res/raw
    
    public Alarm() {
        this.id = (int) System.currentTimeMillis();
        this.hour = 0;
        this.minute = 0;
        this.label = "";
        this.enabled = true;
        this.repeatDays = new boolean[7];
        this.vibrate = true;
        this.ringtoneIndex = 0; // Default to first ringtone
    }
    
    public Alarm(int id, int hour, int minute, String label, boolean enabled, boolean[] repeatDays, boolean vibrate, int ringtoneIndex) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.label = label;
        this.enabled = enabled;
        this.repeatDays = repeatDays != null ? repeatDays : new boolean[7];
        this.vibrate = vibrate;
        this.ringtoneIndex = ringtoneIndex;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getHour() {
        return hour;
    }
    
    public void setHour(int hour) {
        this.hour = hour;
    }
    
    public int getMinute() {
        return minute;
    }
    
    public void setMinute(int minute) {
        this.minute = minute;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean[] getRepeatDays() {
        return repeatDays;
    }
    
    public void setRepeatDays(boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }
    
    public boolean isRepeating() {
        if (repeatDays == null) return false;
        for (boolean day : repeatDays) {
            if (day) return true;
        }
        return false;
    }
    
    public boolean isVibrate() {
        return vibrate;
    }
    
    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }
    
    public int getRingtoneIndex() {
        return ringtoneIndex;
    }
    
    public void setRingtoneIndex(int ringtoneIndex) {
        if (ringtoneIndex >= 0 && ringtoneIndex < 10) {
            this.ringtoneIndex = ringtoneIndex;
        } else {
            this.ringtoneIndex = 0;
        }
    }
    
    public long getNextAlarmTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // If no repeat days, set for tomorrow if time has passed today
        if (!isRepeating()) {
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            return calendar.getTimeInMillis();
        }
        
        // Find next repeating day
        // Calendar.DAY_OF_WEEK: Sunday=1, Monday=2, ..., Saturday=7
        // Our array: Sunday=0, Monday=1, ..., Saturday=6
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentDayIndex = currentDayOfWeek - 1; // Convert to 0-based index
        
        int daysUntilNext = 0;
        boolean found = false;
        
        // Check today and next 7 days
        for (int i = 0; i < 7; i++) {
            int checkDayIndex = (currentDayIndex + i) % 7;
            if (repeatDays[checkDayIndex]) {
                if (i == 0 && calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    // Today's alarm time hasn't passed yet
                    return calendar.getTimeInMillis();
                }
                daysUntilNext = i == 0 ? 7 : i; // If today passed, go to next week
                found = true;
                break;
            }
        }
        
        if (!found) {
            // No repeat day found, shouldn't happen but handle gracefully
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            return calendar.getTimeInMillis();
        }
        
        calendar.add(Calendar.DAY_OF_YEAR, daysUntilNext);
        return calendar.getTimeInMillis();
    }
    
    public String getRepeatText() {
        if (!isRepeating()) {
            return "";
        }
        
        int count = 0;
        for (boolean day : repeatDays) {
            if (day) count++;
        }
        
        if (count == 7) {
            return "Every day";
        }
        
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (repeatDays[i]) {
                if (text.length() > 0) {
                    text.append(", ");
                }
                text.append(dayNames[i]);
            }
        }
        return text.toString();
    }
    
    public String getTimeString() {
        String hourStr = String.format("%02d", hour);
        String minuteStr = String.format("%02d", minute);
        return hourStr + ":" + minuteStr;
    }
}

