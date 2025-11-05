class Alarm {
  int id;
  int hour;
  int minute;
  String label;
  bool enabled;
  List<bool> repeatDays; // 7 days, Sunday = 0, Monday = 1, etc.
  bool vibrate;
  int ringtoneIndex; // Index of ringtone (0-9) stored in assets

  Alarm({
    required this.id,
    this.hour = 0,
    this.minute = 0,
    this.label = '',
    this.enabled = true,
    List<bool>? repeatDays,
    this.vibrate = true,
    this.ringtoneIndex = 0,
  }) : repeatDays = repeatDays ?? List.filled(7, false);

  bool get isRepeating {
    return repeatDays.any((day) => day == true);
  }

  DateTime getNextAlarmTime() {
    final now = DateTime.now();
    var alarmTime = DateTime(now.year, now.month, now.day, hour, minute, 0);

    // If no repeat days, set for tomorrow if time has passed today
    if (!isRepeating) {
      if (alarmTime.isBefore(now) || alarmTime.isAtSameMomentAs(now)) {
        alarmTime = alarmTime.add(const Duration(days: 1));
      }
      return alarmTime;
    }

    // Find next repeating day
    // DateTime.weekday: Monday=1, Tuesday=2, ..., Sunday=7
    // Our array: Sunday=0, Monday=1, ..., Saturday=6
    int currentDayOfWeek = now.weekday; // 1=Monday, 7=Sunday
    int currentDayIndex = currentDayOfWeek == 7 ? 0 : currentDayOfWeek; // Convert to our format

    int daysUntilNext = 0;
    bool found = false;

    // Check today and next 7 days
    for (int i = 0; i < 7; i++) {
      int checkDayIndex = (currentDayIndex + i) % 7;
      if (repeatDays[checkDayIndex]) {
        if (i == 0 && alarmTime.isAfter(now)) {
          // Today's alarm time hasn't passed yet
          return alarmTime;
        }
        daysUntilNext = i == 0 ? 7 : i; // If today passed, go to next week
        found = true;
        break;
      }
    }

    if (!found) {
      // No repeat day found, shouldn't happen but handle gracefully
      return alarmTime.add(const Duration(days: 1));
    }

    return alarmTime.add(Duration(days: daysUntilNext));
  }

  String getRepeatText() {
    if (!isRepeating) {
      return '';
    }

    int count = repeatDays.where((day) => day).length;

    if (count == 7) {
      return 'Every day';
    }

    const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    final text = <String>[];
    for (int i = 0; i < 7; i++) {
      if (repeatDays[i]) {
        text.add(dayNames[i]);
      }
    }
    return text.join(', ');
  }

  String getTimeString() {
    return '${hour.toString().padLeft(2, '0')}:${minute.toString().padLeft(2, '0')}';
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'hour': hour,
      'minute': minute,
      'label': label,
      'enabled': enabled ? 1 : 0,
      'repeat_days': repeatDays.map((d) => d ? '1' : '0').join(''),
      'vibrate': vibrate ? 1 : 0,
      'ringtone_index': ringtoneIndex,
    };
  }

  factory Alarm.fromMap(Map<String, dynamic> map) {
    final repeatDaysStr = map['repeat_days'] as String? ?? '0000000';
    final repeatDays = repeatDaysStr.split('').map((c) => c == '1').toList();
    
    return Alarm(
      id: map['id'] as int,
      hour: map['hour'] as int? ?? 0,
      minute: map['minute'] as int? ?? 0,
      label: map['label'] as String? ?? '',
      enabled: (map['enabled'] as int? ?? 0) == 1,
      repeatDays: repeatDays.length == 7 ? repeatDays : List.filled(7, false),
      vibrate: (map['vibrate'] as int? ?? 0) == 1,
      ringtoneIndex: map['ringtone_index'] as int? ?? 0,
    );
  }
}

