# ALARMv1 - Android Alarm App

A fully functional Android alarm app with the same features as default alarm apps.

## Features

- ✅ Create, edit, and delete alarms
- ✅ Set custom alarm labels
- ✅ Repeat alarms on specific days of the week
- ✅ Enable/disable alarms with a toggle switch
- ✅ Vibrate and sound notifications
- ✅ Snooze functionality (10 minutes)
- ✅ Dismiss alarm
- ✅ Modern Material Design UI
- ✅ Persistent storage using SQLite

## Requirements

- Android Studio
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)

## Permissions

The app requires the following permissions:
- `SCHEDULE_EXACT_ALARM` - For scheduling exact alarm times
- `USE_EXACT_ALARM` - For exact alarm functionality
- `POST_NOTIFICATIONS` - For displaying alarm notifications (Android 13+)
- `WAKE_LOCK` - To wake up the device when alarm rings
- `VIBRATE` - For vibration when alarm rings

## Building the App

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on an Android device or emulator

## Usage

1. **Add Alarm**: Tap the floating action button (+) to create a new alarm
2. **Set Time**: Use the time picker to set the alarm time
3. **Set Label**: Optionally add a label for the alarm
4. **Repeat Days**: Select which days of the week the alarm should repeat
5. **Save**: Tap Save to create the alarm
6. **Edit**: Tap on an alarm to edit it
7. **Toggle**: Use the switch to enable/disable an alarm
8. **Delete**: When editing, tap Delete to remove the alarm

## Alarm Ringing

When an alarm goes off:
- The screen will wake up and display the alarm
- The default alarm sound will play
- Vibration will occur (if enabled)
- You can either:
  - **Snooze**: Postpone the alarm for 10 minutes
  - **Dismiss**: Turn off the alarm

## Project Structure

- `MainActivity.java` - Main activity with alarm list
- `Alarm.java` - Alarm data model
- `AlarmDatabase.java` - SQLite database helper
- `AlarmManagerHelper.java` - Alarm scheduling utility
- `AlarmReceiver.java` - Broadcast receiver for alarm triggers
- `AlarmRingingActivity.java` - Activity shown when alarm rings

