package com.alarmv1;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class RingtoneManager {
    private static final int RINGTONE_COUNT = 10;
    private static RingtoneManager instance;
    private Context context;
    private MediaPlayer previewPlayer;
    
    private RingtoneManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    public static RingtoneManager getInstance(Context context) {
        if (instance == null) {
            instance = new RingtoneManager(context);
        }
        return instance;
    }
    
    public static List<RingtoneInfo> getAvailableRingtones(Context context) {
        List<RingtoneInfo> ringtones = new ArrayList<>();
        
        // 10 ringtones stored in res/raw folder
        String[] ringtoneNames = context.getResources().getStringArray(R.array.ringtone_names);
        int[] ringtoneResources = {
            R.raw.ringtone_1,
            R.raw.ringtone_2,
            R.raw.ringtone_3,
            R.raw.ringtone_4,
            R.raw.ringtone_5,
            R.raw.ringtone_6,
            R.raw.ringtone_7,
            R.raw.ringtone_8,
            R.raw.ringtone_9,
            R.raw.ringtone_10
        };
        
        for (int i = 0; i < RINGTONE_COUNT && i < ringtoneResources.length; i++) {
            String name = i < ringtoneNames.length ? ringtoneNames[i] : "Ringtone " + (i + 1);
            ringtones.add(new RingtoneInfo(i, name, ringtoneResources[i]));
        }
        
        return ringtones;
    }
    
    public Uri getRingtoneUri(int ringtoneIndex) {
        if (ringtoneIndex < 0 || ringtoneIndex >= RINGTONE_COUNT) {
            return null;
        }
        
        int[] ringtoneResources = {
            R.raw.ringtone_1,
            R.raw.ringtone_2,
            R.raw.ringtone_3,
            R.raw.ringtone_4,
            R.raw.ringtone_5,
            R.raw.ringtone_6,
            R.raw.ringtone_7,
            R.raw.ringtone_8,
            R.raw.ringtone_9,
            R.raw.ringtone_10
        };
        
        if (ringtoneIndex < ringtoneResources.length) {
            return Uri.parse("android.resource://" + context.getPackageName() + "/" + ringtoneResources[ringtoneIndex]);
        }
        
        return null;
    }
    
    public int getRingtoneResource(int ringtoneIndex) {
        if (ringtoneIndex < 0 || ringtoneIndex >= RINGTONE_COUNT) {
            return -1;
        }
        
        int[] ringtoneResources = {
            R.raw.ringtone_1,
            R.raw.ringtone_2,
            R.raw.ringtone_3,
            R.raw.ringtone_4,
            R.raw.ringtone_5,
            R.raw.ringtone_6,
            R.raw.ringtone_7,
            R.raw.ringtone_8,
            R.raw.ringtone_9,
            R.raw.ringtone_10
        };
        
        if (ringtoneIndex < ringtoneResources.length) {
            return ringtoneResources[ringtoneIndex];
        }
        
        return -1;
    }
    
    public String getRingtoneName(int ringtoneIndex) {
        if (ringtoneIndex < 0 || ringtoneIndex >= RINGTONE_COUNT) {
            return "Default";
        }
        
        String[] ringtoneNames = context.getResources().getStringArray(R.array.ringtone_names);
        if (ringtoneIndex < ringtoneNames.length) {
            return ringtoneNames[ringtoneIndex];
        }
        
        return "Ringtone " + (ringtoneIndex + 1);
    }
    
    public void previewRingtone(int ringtoneIndex) {
        stopPreview();
        
        int resourceId = getRingtoneResource(ringtoneIndex);
        if (resourceId == -1) {
            return;
        }
        
        try {
            previewPlayer = MediaPlayer.create(context, resourceId);
            if (previewPlayer != null) {
                previewPlayer.setLooping(false);
                previewPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopPreview() {
        if (previewPlayer != null) {
            try {
                previewPlayer.stop();
                previewPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            previewPlayer = null;
        }
    }
    
    public static class RingtoneInfo {
        private int index;
        private String name;
        private int resourceId;
        
        public RingtoneInfo(int index, String name, int resourceId) {
            this.index = index;
            this.name = name;
            this.resourceId = resourceId;
        }
        
        public int getIndex() {
            return index;
        }
        
        public String getName() {
            return name;
        }
        
        public int getResourceId() {
            return resourceId;
        }
    }
}

