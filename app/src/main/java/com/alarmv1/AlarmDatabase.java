package com.alarmv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_ALARMS = "alarms";
    
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HOUR = "hour";
    private static final String COLUMN_MINUTE = "minute";
    private static final String COLUMN_LABEL = "label";
    private static final String COLUMN_ENABLED = "enabled";
    private static final String COLUMN_REPEAT_DAYS = "repeat_days";
    private static final String COLUMN_VIBRATE = "vibrate";
    private static final String COLUMN_RINGTONE_INDEX = "ringtone_index";
    
    public AlarmDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ALARMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_HOUR + " INTEGER, " +
                COLUMN_MINUTE + " INTEGER, " +
                COLUMN_LABEL + " TEXT, " +
                COLUMN_ENABLED + " INTEGER, " +
                COLUMN_REPEAT_DAYS + " TEXT, " +
                COLUMN_VIBRATE + " INTEGER, " +
                COLUMN_RINGTONE_INDEX + " INTEGER)";
        db.execSQL(createTable);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Migrate from ringtone_uri to ringtone_index
            db.execSQL("ALTER TABLE " + TABLE_ALARMS + " ADD COLUMN " + COLUMN_RINGTONE_INDEX + " INTEGER DEFAULT 0");
            // Drop old column if it exists (SQLite doesn't support DROP COLUMN directly)
            // We'll recreate the table for migration
            db.execSQL("CREATE TABLE " + TABLE_ALARMS + "_new (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_HOUR + " INTEGER, " +
                    COLUMN_MINUTE + " INTEGER, " +
                    COLUMN_LABEL + " TEXT, " +
                    COLUMN_ENABLED + " INTEGER, " +
                    COLUMN_REPEAT_DAYS + " TEXT, " +
                    COLUMN_VIBRATE + " INTEGER, " +
                    COLUMN_RINGTONE_INDEX + " INTEGER)");
            db.execSQL("INSERT INTO " + TABLE_ALARMS + "_new SELECT " +
                    COLUMN_ID + ", " + COLUMN_HOUR + ", " + COLUMN_MINUTE + ", " +
                    COLUMN_LABEL + ", " + COLUMN_ENABLED + ", " + COLUMN_REPEAT_DAYS + ", " +
                    COLUMN_VIBRATE + ", 0 FROM " + TABLE_ALARMS);
            db.execSQL("DROP TABLE " + TABLE_ALARMS);
            db.execSQL("ALTER TABLE " + TABLE_ALARMS + "_new RENAME TO " + TABLE_ALARMS);
        }
    }
    
    public void addAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, alarm.getId());
        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_LABEL, alarm.getLabel());
        values.put(COLUMN_ENABLED, alarm.isEnabled() ? 1 : 0);
        values.put(COLUMN_REPEAT_DAYS, booleanArrayToString(alarm.getRepeatDays()));
        values.put(COLUMN_VIBRATE, alarm.isVibrate() ? 1 : 0);
        values.put(COLUMN_RINGTONE_INDEX, alarm.getRingtoneIndex());
        
        db.insert(TABLE_ALARMS, null, values);
        db.close();
    }
    
    public void updateAlarm(Alarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HOUR, alarm.getHour());
        values.put(COLUMN_MINUTE, alarm.getMinute());
        values.put(COLUMN_LABEL, alarm.getLabel());
        values.put(COLUMN_ENABLED, alarm.isEnabled() ? 1 : 0);
        values.put(COLUMN_REPEAT_DAYS, booleanArrayToString(alarm.getRepeatDays()));
        values.put(COLUMN_VIBRATE, alarm.isVibrate() ? 1 : 0);
        values.put(COLUMN_RINGTONE_INDEX, alarm.getRingtoneIndex());
        
        db.update(TABLE_ALARMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(alarm.getId())});
        db.close();
    }
    
    public void deleteAlarm(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    public Alarm getAlarm(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARMS, null, COLUMN_ID + " = ?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        Alarm alarm = null;
        if (cursor.moveToFirst()) {
            alarm = cursorToAlarm(cursor);
        }
        cursor.close();
        db.close();
        return alarm;
    }
    
    public List<Alarm> getAllAlarms() {
        List<Alarm> alarms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARMS, null, null, null, null, null, 
                COLUMN_HOUR + ", " + COLUMN_MINUTE);
        
        if (cursor.moveToFirst()) {
            do {
                alarms.add(cursorToAlarm(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return alarms;
    }
    
    private Alarm cursorToAlarm(Cursor cursor) {
        Alarm alarm = new Alarm();
        alarm.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        alarm.setHour(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOUR)));
        alarm.setMinute(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTE)));
        alarm.setLabel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LABEL)));
        alarm.setEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENABLED)) == 1);
        alarm.setRepeatDays(stringToBooleanArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPEAT_DAYS))));
        alarm.setVibrate(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIBRATE)) == 1);
        
        // Handle migration from old ringtone_uri to ringtone_index
        try {
            alarm.setRingtoneIndex(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RINGTONE_INDEX)));
        } catch (IllegalArgumentException e) {
            // Column doesn't exist, use default
            alarm.setRingtoneIndex(0);
        }
        
        return alarm;
    }
    
    private String booleanArrayToString(boolean[] array) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : array) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }
    
    private boolean[] stringToBooleanArray(String str) {
        if (str == null || str.length() != 7) {
            return new boolean[7];
        }
        boolean[] array = new boolean[7];
        for (int i = 0; i < 7; i++) {
            array[i] = str.charAt(i) == '1';
        }
        return array;
    }
}

