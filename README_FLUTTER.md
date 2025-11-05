# ALARMv1 - Flutter Alarm App

A fully functional Flutter alarm app with the same features as default alarm apps.

## Features

- ✅ Create, edit, and delete alarms
- ✅ Set custom alarm labels
- ✅ Repeat alarms on specific days of the week
- ✅ Enable/disable alarms with a toggle switch
- ✅ 10 bundled ringtones to choose from
- ✅ Vibrate and sound notifications
- ✅ Snooze functionality (10 minutes)
- ✅ Dismiss alarm
- ✅ Modern Material Design UI
- ✅ Persistent storage using SQLite

## Requirements

- Flutter SDK (3.0.0 or higher)
- Android Studio / VS Code
- Android SDK (API 24+)

## Setup

### 1. Install Dependencies

```bash
flutter pub get
```

### 2. Add Audio Files

Place your 10 ringtone audio files in:
```
assets/ringtones/
├── ringtone_1.mp3
├── ringtone_2.mp3
├── ringtone_3.mp3
├── ringtone_4.mp3
├── ringtone_5.mp3
├── ringtone_6.mp3
├── ringtone_7.mp3
├── ringtone_8.mp3
├── ringtone_9.mp3
└── ringtone_10.mp3
```

Supported formats: MP3, OGG, WAV

### 3. Run the App

```bash
flutter run
```

## Permissions

The app requires the following permissions (automatically requested):
- `SCHEDULE_EXACT_ALARM` - For scheduling exact alarm times
- `USE_EXACT_ALARM` - For exact alarm functionality
- `POST_NOTIFICATIONS` - For displaying alarm notifications (Android 13+)
- `WAKE_LOCK` - To wake up the device when alarm rings
- `VIBRATE` - For vibration when alarm rings

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

## Usage

1. **Add Alarm**: Tap the floating action button (+) to create a new alarm
2. **Set Time**: Tap the time display to open time picker
3. **Set Label**: Optionally add a label for the alarm
4. **Repeat Days**: Select which days of the week the alarm should repeat
5. **Select Ringtone**: Tap "Ringtone" to choose from 10 bundled ringtones
6. **Preview**: Tap "Preview" button to hear a ringtone before selecting
7. **Save**: Tap Save to create the alarm
8. **Edit**: Tap on an alarm to edit it
9. **Toggle**: Use the switch to enable/disable an alarm
10. **Delete**: When editing, tap the delete icon to remove the alarm

## Alarm Ringing

When an alarm goes off:
- The screen will wake up and display the alarm
- The selected ringtone will play
- Vibration will occur (if enabled)
- You can either:
  - **Snooze**: Postpone the alarm for 10 minutes
  - **Dismiss**: Turn off the alarm

## Dependencies

- `sqflite`: SQLite database
- `flutter_local_notifications`: Alarm scheduling
- `timezone`: Timezone handling
- `audioplayers`: Audio playback
- `vibration`: Vibration control
- `intl`: Internationalization

## Troubleshooting

### Alarms Not Triggering
- Grant notification permissions when prompted
- Check device battery optimization settings
- Verify exact alarm permissions are granted

### Audio Not Playing
- Ensure audio files are in `assets/ringtones/`
- Check file format (MP3, OGG, WAV)
- Verify assets are declared in `pubspec.yaml`

### Build Errors
```bash
flutter clean
flutter pub get
flutter run
```

## Notes

- Ringtones are stored securely in app assets
- No file paths are exposed in the frontend
- Ringtone selection is stored as index (0-9) in database
- All audio files are bundled with the app

