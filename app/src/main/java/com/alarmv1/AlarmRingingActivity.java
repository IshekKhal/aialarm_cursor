package com.alarmv1;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmRingingActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private int alarmId;
    private String alarmLabel;
    private boolean shouldVibrate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Keep screen on and wake up device
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        setContentView(R.layout.activity_alarm_ringing);
        
        // Get alarm data from intent
        alarmId = getIntent().getIntExtra("alarm_id", -1);
        int hour = getIntent().getIntExtra("alarm_hour", 0);
        int minute = getIntent().getIntExtra("alarm_minute", 0);
        alarmLabel = getIntent().getStringExtra("alarm_label");
        shouldVibrate = getIntent().getBooleanExtra("alarm_vibrate", true);
        int ringtoneIndex = getIntent().getIntExtra("alarm_ringtone_index", 0);
        
        TextView timeTextView = findViewById(R.id.alarm_time_text);
        TextView labelTextView = findViewById(R.id.alarm_label_text);
        Button dismissButton = findViewById(R.id.btn_dismiss);
        Button snoozeButton = findViewById(R.id.btn_snooze);
        
        String timeString = String.format("%02d:%02d", hour, minute);
        timeTextView.setText(timeString);
        
        if (alarmLabel != null && !alarmLabel.isEmpty()) {
            labelTextView.setText(alarmLabel);
            labelTextView.setVisibility(View.VISIBLE);
        } else {
            labelTextView.setVisibility(View.GONE);
        }
        
        // Start alarm sound
        startAlarmSound(ringtoneIndex);
        
        // Start vibration
        if (shouldVibrate) {
            startVibration();
        }
        
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAlarm();
            }
        });
        
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snoozeAlarm();
            }
        });
    }
    
    private void startAlarmSound(int ringtoneIndex) {
        try {
            RingtoneManager ringtoneManager = RingtoneManager.getInstance(this);
            int resourceId = ringtoneManager.getRingtoneResource(ringtoneIndex);
            
            if (resourceId != -1) {
                mediaPlayer = MediaPlayer.create(this, resourceId);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            } else {
                // Fallback to default alarm
                android.media.RingtoneManager ringtoneMgr = new android.media.RingtoneManager(this);
                Uri alarmUri = ringtoneMgr.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = ringtoneMgr.getDefaultUri(android.media.RingtoneManager.TYPE_RINGTONE);
                }
                mediaPlayer = MediaPlayer.create(this, alarmUri);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void startVibration() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 1000, 500, 1000, 500, 1000};
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, 0);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }
    }
    
    private void dismissAlarm() {
        stopAlarm();
        finish();
    }
    
    private void snoozeAlarm() {
        stopAlarm();
        
        // Schedule alarm for 10 minutes later
        AlarmDatabase alarmDatabase = new AlarmDatabase(this);
        Alarm alarm = alarmDatabase.getAlarm(alarmId);
        if (alarm != null) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.add(java.util.Calendar.MINUTE, 10);
            alarm.setHour(calendar.get(java.util.Calendar.HOUR_OF_DAY));
            alarm.setMinute(calendar.get(java.util.Calendar.MINUTE));
            alarm.setRepeatDays(new boolean[7]); // Clear repeat for snooze
            AlarmManagerHelper.setAlarm(alarm);
        }
        
        finish();
    }
    
    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
    
    @Override
    public void onBackPressed() {
        // Prevent dismissing alarm by back button - must use dismiss button
    }
}

