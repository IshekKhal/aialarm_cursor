import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/timezone.dart' as tz;
import 'package:timezone/data/latest_all.dart' as tz_data;
import '../models/alarm.dart';
import 'ringtone_manager.dart';

class AlarmManagerHelper {
  static final AlarmManagerHelper instance = AlarmManagerHelper._init();
  AlarmManagerHelper._init();

  final FlutterLocalNotificationsPlugin _notifications = FlutterLocalNotificationsPlugin();
  bool _initialized = false;

  Future<void> initialize() async {
    if (_initialized) return;
    
    tz_data.initializeTimeZones();
    
    const androidSettings = AndroidInitializationSettings('@mipmap/ic_launcher');
    const iosSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
      defaultPresentAlert: true,
      defaultPresentSound: true,
      defaultPresentBadge: true,
    );
    const initSettings = InitializationSettings(
      android: androidSettings,
      iOS: iosSettings,
    );

    await _notifications.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationTapped,
    );

    // Explicitly request iOS permissions (iOS 10+)
    final darwinPlugin = _notifications
        .resolvePlatformSpecificImplementation<DarwinFlutterLocalNotificationsPlugin>();
    await darwinPlugin?.requestPermissions(
      alert: true,
      badge: true,
      sound: true,
    );

    _initialized = true;
  }

  void _onNotificationTapped(NotificationResponse response) {
    // Handle notification tap - navigate to alarm ringing screen
    // This will be handled by the app's navigation
  }

  Future<void> setAlarm(Alarm alarm) async {
    if (!_initialized) await initialize();
    
    if (!alarm.enabled) {
      await cancelAlarm(alarm);
      return;
    }

    final alarmTime = alarm.getNextAlarmTime();
    final ringtonePath = RingtoneManager.instance.getRingtoneAssetPath(alarm.ringtoneIndex);

    const androidDetails = AndroidNotificationDetails(
      'alarm_channel',
      'Alarm Notifications',
      channelDescription: 'Channel for alarm notifications',
      importance: Importance.high,
      priority: Priority.high,
      fullScreenIntent: true,
      playSound: true,
      sound: RawResourceAndroidNotificationSound('ringtone_${alarm.ringtoneIndex + 1}'),
    );

    final iosDetails = DarwinNotificationDetails(
      sound: RingtoneManager.instance.getIosNotificationSound(alarm.ringtoneIndex),
    );
    final notificationDetails = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _notifications.zonedSchedule(
      alarm.id,
      alarm.label.isNotEmpty ? alarm.label : 'Alarm',
      alarm.getTimeString(),
      tz.TZDateTime.from(alarmTime, tz.local),
      notificationDetails,
      androidScheduleMode: AndroidScheduleMode.exactAllowWhileIdle,
      uiLocalNotificationDateInterpretation: UILocalNotificationDateInterpretation.absoluteTime,
      payload: alarm.id.toString(),
    );

    // For repeating alarms, schedule additional notifications
    if (alarm.isRepeating) {
      await _scheduleRepeatingAlarms(alarm);
    }
  }

  Future<void> _scheduleRepeatingAlarms(Alarm alarm) async {
    final now = DateTime.now();
    final baseTime = DateTime(now.year, now.month, now.day, alarm.hour, alarm.minute, 0);
    
    // DateTime.weekday: Monday=1, Tuesday=2, ..., Sunday=7
    // Our array: Sunday=0, Monday=1, ..., Saturday=6
    int currentDayOfWeek = now.weekday;
    int currentDayIndex = currentDayOfWeek == 7 ? 0 : currentDayOfWeek;

    for (int i = 0; i < 7; i++) {
      if (alarm.repeatDays[i]) {
        int daysUntil = (i - currentDayIndex + 7) % 7;
        if (daysUntil == 0 && baseTime.isBefore(now) || baseTime.isAtSameMomentAs(now)) {
          daysUntil = 7; // Next week if time has passed today
        }

        final repeatTime = baseTime.add(Duration(days: daysUntil));
        final repeatId = alarm.id * 100 + i; // Unique ID for each repeat
        
        const androidDetails = AndroidNotificationDetails(
          'alarm_channel',
          'Alarm Notifications',
          channelDescription: 'Channel for alarm notifications',
          importance: Importance.high,
          priority: Priority.high,
          fullScreenIntent: true,
          playSound: true,
          sound: RawResourceAndroidNotificationSound('ringtone_${alarm.ringtoneIndex + 1}'),
        );

        final iosDetails = DarwinNotificationDetails(
          sound: RingtoneManager.instance.getIosNotificationSound(alarm.ringtoneIndex),
        );
        final notificationDetails = NotificationDetails(
          android: androidDetails,
          iOS: iosDetails,
        );

        await _notifications.zonedSchedule(
          repeatId,
          alarm.label.isNotEmpty ? alarm.label : 'Alarm',
          alarm.getTimeString(),
          tz.TZDateTime.from(repeatTime, tz.local),
          notificationDetails,
          androidScheduleMode: AndroidScheduleMode.exactAllowWhileIdle,
          uiLocalNotificationDateInterpretation: UILocalNotificationDateInterpretation.absoluteTime,
          payload: alarm.id.toString(),
        );
      }
    }
  }

  Future<void> cancelAlarm(Alarm alarm) async {
    if (!_initialized) await initialize();
    
    await _notifications.cancel(alarm.id);
    
    // Cancel repeating alarms
    if (alarm.isRepeating) {
      for (int i = 0; i < 7; i++) {
        if (alarm.repeatDays[i]) {
          final repeatId = alarm.id * 100 + i;
          await _notifications.cancel(repeatId);
        }
      }
    }
  }
}

