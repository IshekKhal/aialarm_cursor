# Flutter Migration Guide

The Android alarm app has been successfully converted to Flutter. This document provides information about the migration and setup.

## Project Structure

```
lib/
├── main.dart                          # App entry point
├── models/
│   └── alarm.dart                    # Alarm data model
├── services/
│   ├── alarm_database.dart           # SQLite database helper
│   ├── alarm_manager_helper.dart     # Alarm scheduling service
│   └── ringtone_manager.dart         # Ringtone management
├── screens/
│   ├── main_screen.dart              # Main alarm list screen
│   ├── add_alarm_screen.dart         # Add/Edit alarm screen
│   ├── ringtone_selection_screen.dart # Ringtone selection
│   └── alarm_ringing_screen.dart     # Full-screen alarm screen
└── widgets/
    └── alarm_item.dart               # Alarm list item widget
```

## Key Changes from Android

### 1. **Language & Framework**
- **Android**: Java + XML layouts
- **Flutter**: Dart + Widget-based UI

### 2. **Database**
- **Android**: SQLiteOpenHelper
- **Flutter**: sqflite package

### 3. **Alarm Scheduling**
- **Android**: AlarmManager + BroadcastReceiver
- **Flutter**: flutter_local_notifications + timezone

### 4. **Audio Playback**
- **Android**: MediaPlayer
- **Flutter**: audioplayers package

### 5. **Vibration**
- **Android**: Vibrator service
- **Flutter**: vibration package

## Setup Instructions

### 1. Install Dependencies

```bash
flutter pub get
```

### 2. Add Audio Files

Place your 10 ringtone files in:
```
assets/ringtones/
├── ringtone_1.mp3
├── ringtone_2.mp3
├── ...
└── ringtone_10.mp3
```

### 3. Android Configuration

The Android manifest has been configured with all necessary permissions:
- `SCHEDULE_EXACT_ALARM`
- `USE_EXACT_ALARM`
- `POST_NOTIFICATIONS`
- `WAKE_LOCK`
- `VIBRATE`

### 4. Run the App

```bash
flutter run
```

## Features

✅ All features from Android version:
- Create, edit, delete alarms
- Custom labels
- Repeat on specific days
- Enable/disable toggle
- 10 bundled ringtones
- Vibrate and sound
- Snooze (10 minutes)
- Dismiss alarm
- Full-screen alarm screen

## Dependencies

- `sqflite`: SQLite database
- `flutter_local_notifications`: Alarm scheduling
- `timezone`: Timezone handling
- `audioplayers`: Audio playback
- `vibration`: Vibration control
- `intl`: Internationalization

## Notes

1. **Notification Handling**: The app uses `flutter_local_notifications` for alarm scheduling. On Android 12+, exact alarms require special permissions.

2. **Assets**: Audio files must be in `assets/ringtones/` directory and declared in `pubspec.yaml`.

3. **Time Zone**: The app uses the `timezone` package for accurate time zone handling.

4. **Platform Support**: Currently configured for Android. iOS support can be added with additional configuration.

## Troubleshooting

### Alarms Not Triggering
- Check notification permissions (Android 13+)
- Verify exact alarm permissions are granted
- Check device's battery optimization settings

### Audio Not Playing
- Ensure audio files are in `assets/ringtones/`
- Check file format (MP3, OGG, WAV supported)
- Verify asset paths in `pubspec.yaml`

### Build Errors
- Run `flutter clean`
- Run `flutter pub get`
- Rebuild the project

