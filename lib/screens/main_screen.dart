import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/alarm.dart';
import '../services/alarm_database.dart';
import '../services/alarm_manager_helper.dart';
import 'add_alarm_screen.dart';
import '../widgets/alarm_item.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  final AlarmDatabase _database = AlarmDatabase.instance;
  final AlarmManagerHelper _alarmManager = AlarmManagerHelper.instance;
  List<Alarm> _alarms = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _initializeApp();
    _loadAlarms();
  }

  Future<void> _initializeApp() async {
    await _alarmManager.initialize();
  }

  Future<void> _loadAlarms() async {
    setState(() => _isLoading = true);
    final alarms = await _database.getAllAlarms();
    setState(() {
      _alarms = alarms;
      _isLoading = false;
    });
  }

  Future<void> _addAlarm() async {
    final result = await Navigator.push<Alarm>(
      context,
      MaterialPageRoute(builder: (context) => const AddAlarmScreen()),
    );

    if (result != null) {
      await _database.addAlarm(result);
      await _alarmManager.setAlarm(result);
      await _loadAlarms();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Alarm added')),
        );
      }
    }
  }

  Future<void> _editAlarm(Alarm alarm) async {
    final result = await Navigator.push<Alarm>(
      context,
      MaterialPageRoute(builder: (context) => AddAlarmScreen(alarm: alarm)),
    );

    if (result != null) {
      await _alarmManager.cancelAlarm(alarm);
      await _database.updateAlarm(result);
      if (result.enabled) {
        await _alarmManager.setAlarm(result);
      }
      await _loadAlarms();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Alarm updated')),
        );
      }
    }
  }

  Future<void> _deleteAlarm(Alarm alarm) async {
    await _alarmManager.cancelAlarm(alarm);
    await _database.deleteAlarm(alarm.id);
    await _loadAlarms();
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Alarm deleted')),
      );
    }
  }

  Future<void> _toggleAlarm(Alarm alarm) async {
    alarm.enabled = !alarm.enabled;
    await _database.updateAlarm(alarm);
    if (alarm.enabled) {
      await _alarmManager.setAlarm(alarm);
    } else {
      await _alarmManager.cancelAlarm(alarm);
    }
    await _loadAlarms();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Alarms'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _alarms.isEmpty
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.alarm_off,
                        size: 64,
                        color: Colors.grey[400],
                      ),
                      const SizedBox(height: 16),
                      Text(
                        'No alarms set',
                        style: TextStyle(
                          fontSize: 18,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                )
              : ListView.builder(
                  padding: const EdgeInsets.all(8),
                  itemCount: _alarms.length,
                  itemBuilder: (context, index) {
                    final alarm = _alarms[index];
                    return AlarmItem(
                      alarm: alarm,
                      onTap: () => _editAlarm(alarm),
                      onToggle: () => _toggleAlarm(alarm),
                    );
                  },
                ),
      floatingActionButton: FloatingActionButton(
        onPressed: _addAlarm,
        child: const Icon(Icons.add),
      ),
    );
  }
}

