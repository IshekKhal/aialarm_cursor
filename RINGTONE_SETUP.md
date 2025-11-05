# Ringtone Setup Instructions

## Overview
The app includes 10 bundled alarm ringtones that are stored securely in the app's resources. The audio files are stored in the `res/raw` folder and are not exposed to users - they can only be selected through the app's interface.

## Adding Your Ringtone Audio Files

### Step 1: Prepare Audio Files
You need 10 audio files in one of these formats:
- **MP3** (recommended)
- **OGG** (recommended for Android)
- **WAV**

Name them as follows:
- `ringtone_1.mp3` (or .ogg, .wav)
- `ringtone_2.mp3`
- `ringtone_3.mp3`
- `ringtone_4.mp3`
- `ringtone_5.mp3`
- `ringtone_6.mp3`
- `ringtone_7.mp3`
- `ringtone_8.mp3`
- `ringtone_9.mp3`
- `ringtone_10.mp3`

### Step 2: Create the res/raw Directory
1. Navigate to `app/src/main/res/`
2. Create a new folder named `raw` (if it doesn't exist)
3. The full path should be: `app/src/main/res/raw/`

### Step 3: Add Audio Files
1. Copy your 10 audio files into the `app/src/main/res/raw/` directory
2. Make sure the file names match exactly: `ringtone_1`, `ringtone_2`, etc.
3. Android will automatically generate resource IDs (R.raw.ringtone_1, etc.)

### Step 4: Verify Resource IDs
The app expects these resource IDs:
- `R.raw.ringtone_1`
- `R.raw.ringtone_2`
- `R.raw.ringtone_3`
- `R.raw.ringtone_4`
- `R.raw.ringtone_5`
- `R.raw.ringtone_6`
- `R.raw.ringtone_7`
- `R.raw.ringtone_8`
- `R.raw.ringtone_9`
- `R.raw.ringtone_10`

After adding the files, rebuild the project. Android Studio will generate these resource IDs automatically.

### Step 5: Customize Ringtone Names (Optional)
The ringtone names are defined in `app/src/main/res/values/arrays.xml`:

```xml
<string-array name="ringtone_names">
    <item>Classic Alarm</item>
    <item>Morning Breeze</item>
    <item>Digital Wake</item>
    <item>Gentle Chime</item>
    <item>Nature Sounds</item>
    <item>Electronic Pulse</item>
    <item>Soft Melody</item>
    <item>Rising Sun</item>
    <item>Peaceful Dawn</item>
    <item>Energize</item>
</string-array>
```

You can customize these names to match your audio files.

## File Structure
```
app/src/main/res/
├── raw/
│   ├── ringtone_1.mp3
│   ├── ringtone_2.mp3
│   ├── ringtone_3.mp3
│   ├── ringtone_4.mp3
│   ├── ringtone_5.mp3
│   ├── ringtone_6.mp3
│   ├── ringtone_7.mp3
│   ├── ringtone_8.mp3
│   ├── ringtone_9.mp3
│   └── ringtone_10.mp3
```

## Security
- Audio files are stored in the app's APK (in res/raw)
- Files are not accessible to users or other apps
- No file paths are exposed in the frontend
- Ringtone selection is stored as an index (0-9) in the database
- All audio files are bundled with the app

## Notes
- Audio files should be optimized for alarms (typically 30-60 seconds)
- Recommended format: OGG Vorbis or MP3
- File size: Keep files under 1MB each for better app performance
- If you add fewer than 10 files, the app will only show available ringtones

## Troubleshooting

### Files Not Found
If you get "Resource not found" errors:
1. Make sure files are in `app/src/main/res/raw/` (not `app/src/main/assets/`)
2. Rebuild the project (Build > Rebuild Project)
3. Check that file names match exactly: `ringtone_1`, `ringtone_2`, etc.
4. Verify file extensions are supported (.mp3, .ogg, .wav)

### Ringtone Not Playing
1. Check that the audio file is not corrupted
2. Verify the file format is supported
3. Test the file in a media player first
4. Check Android logs for MediaPlayer errors

