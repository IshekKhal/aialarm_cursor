import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:vibration/vibration.dart';
import '../models/alarm.dart';
import '../services/alarm_database.dart';
import '../services/alarm_manager_helper.dart';
import '../services/ringtone_manager.dart';

class AlarmRingingScreen extends StatefulWidget {
  final int alarmId;

  const AlarmRingingScreen({super.key, required this.alarmId});

  @override
  State<AlarmRingingScreen> createState() => _AlarmRingingScreenState();
}

class _AlarmRingingScreenState extends State<AlarmRingingScreen> {
  Alarm? _alarm;
  AudioPlayer? _audioPlayer;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadAlarm();
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersive);
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
  }

  @override
  void dispose() {
    _stopAlarm();
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
    SystemChrome.setPreferredOrientations(DeviceOrientation.values);
    super.dispose();
  }

  Future<void> _loadAlarm() async {
    final alarm = await AlarmDatabase.instance.getAlarm(widget.alarmId);
    setState(() {
      _alarm = alarm;
      _isLoading = false;
    });

    if (alarm != null) {
      _startAlarm(alarm);
    }
  }

  Future<void> _startAlarm(Alarm alarm) async {
    // Start vibration
    if (alarm.vibrate && await Vibration.hasVibrator() == true) {
      Vibration.vibrate(pattern: [0, 1000, 500, 1000, 500, 1000], repeat: 0);
    }

    // Start audio
    final ringtoneManager = RingtoneManager.instance;
    final assetPath = ringtoneManager.getRingtoneAssetPath(alarm.ringtoneIndex);
    
    _audioPlayer = AudioPlayer();
    await _audioPlayer!.setReleaseMode(ReleaseMode.loop);
    await _audioPlayer!.play(AssetSource(assetPath.replaceFirst('assets/', '')));
  }

  Future<void> _stopAlarm() async {
    await Vibration.cancel();
    await _audioPlayer?.stop();
    await _audioPlayer?.dispose();
    _audioPlayer = null;
  }

  Future<void> _dismissAlarm() async {
    await _stopAlarm();
    if (_alarm != null) {
      _alarm!.enabled = false;
      await AlarmDatabase.instance.updateAlarm(_alarm!);
      await AlarmManagerHelper.instance.cancelAlarm(_alarm!);
    }
    if (mounted) {
      Navigator.of(context).popUntil((route) => route.isFirst);
    }
  }

  Future<void> _snoozeAlarm() async {
    await _stopAlarm();
    if (_alarm != null) {
      final now = DateTime.now();
      final snoozeTime = now.add(const Duration(minutes: 10));
      
      final snoozeAlarm = Alarm(
        id: DateTime.now().millisecondsSinceEpoch,
        hour: snoozeTime.hour,
        minute: snoozeTime.minute,
        label: _alarm!.label,
        enabled: true,
        vibrate: _alarm!.vibrate,
        ringtoneIndex: _alarm!.ringtoneIndex,
      );

      await AlarmDatabase.instance.addAlarm(snoozeAlarm);
      await AlarmManagerHelper.instance.setAlarm(snoozeAlarm);
    }
    if (mounted) {
      Navigator.of(context).popUntil((route) => route.isFirst);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading || _alarm == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                _alarm!.getTimeString(),
                style: const TextStyle(
                  fontSize: 72,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                ),
              ),
              if (_alarm!.label.isNotEmpty) ...[
                const SizedBox(height: 16),
                Text(
                  _alarm!.label,
                  style: const TextStyle(
                    fontSize: 24,
                    color: Colors.white,
                  ),
                ),
              ],
              const SizedBox(height: 48),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton.icon(
                    onPressed: _snoozeAlarm,
                    icon: const Icon(Icons.snooze),
                    label: const Text('Snooze'),
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                      textStyle: const TextStyle(fontSize: 18),
                    ),
                  ),
                  const SizedBox(width: 16),
                  ElevatedButton.icon(
                    onPressed: _dismissAlarm,
                    icon: const Icon(Icons.close),
                    label: const Text('Dismiss'),
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                      textStyle: const TextStyle(fontSize: 18),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

