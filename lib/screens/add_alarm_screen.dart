import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/alarm.dart';
import '../services/ringtone_manager.dart';
import 'ringtone_selection_screen.dart';

class AddAlarmScreen extends StatefulWidget {
  final Alarm? alarm;

  const AddAlarmScreen({super.key, this.alarm});

  @override
  State<AddAlarmScreen> createState() => _AddAlarmScreenState();
}

class _AddAlarmScreenState extends State<AddAlarmScreen> {
  late TimeOfDay _selectedTime;
  late TextEditingController _labelController;
  late List<bool> _repeatDays;
  late int _selectedRingtoneIndex;
  final RingtoneManager _ringtoneManager = RingtoneManager.instance;

  @override
  void initState() {
    super.initState();
    if (widget.alarm != null) {
      _selectedTime = TimeOfDay(hour: widget.alarm!.hour, minute: widget.alarm!.minute);
      _labelController = TextEditingController(text: widget.alarm!.label);
      _repeatDays = List.from(widget.alarm!.repeatDays);
      _selectedRingtoneIndex = widget.alarm!.ringtoneIndex;
    } else {
      final now = DateTime.now();
      _selectedTime = TimeOfDay(hour: now.hour, minute: now.minute);
      _labelController = TextEditingController();
      _repeatDays = List.filled(7, false);
      _selectedRingtoneIndex = 0;
    }
  }

  @override
  void dispose() {
    _labelController.dispose();
    super.dispose();
  }

  Future<void> _selectTime() async {
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: _selectedTime,
    );
    if (picked != null && picked != _selectedTime) {
      setState(() {
        _selectedTime = picked;
      });
    }
  }

  Future<void> _selectRingtone() async {
    final result = await Navigator.push<int>(
      context,
      MaterialPageRoute(
        builder: (context) => RingtoneSelectionScreen(
          selectedIndex: _selectedRingtoneIndex,
        ),
      ),
    );

    if (result != null) {
      setState(() {
        _selectedRingtoneIndex = result;
      });
    }
  }

  void _saveAlarm() {
    final alarm = widget.alarm ?? Alarm(id: DateTime.now().millisecondsSinceEpoch);
    alarm.hour = _selectedTime.hour;
    alarm.minute = _selectedTime.minute;
    alarm.label = _labelController.text;
    alarm.repeatDays = _repeatDays;
    alarm.ringtoneIndex = _selectedRingtoneIndex;

    Navigator.pop(context, alarm);
  }

  void _deleteAlarm() {
    if (widget.alarm != null) {
      Navigator.pop(context, null);
    }
  }

  @override
  Widget build(BuildContext context) {
    final isEditing = widget.alarm != null;
    const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

    return Scaffold(
      appBar: AppBar(
        title: Text(isEditing ? 'Edit Alarm' : 'New Alarm'),
        actions: [
          if (isEditing)
            IconButton(
              icon: const Icon(Icons.delete),
              onPressed: _deleteAlarm,
              tooltip: 'Delete',
            ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Time Picker
            Center(
              child: GestureDetector(
                onTap: _selectTime,
                child: Container(
                  padding: const EdgeInsets.all(20),
                  decoration: BoxDecoration(
                    border: Border.all(color: Colors.grey),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    _selectedTime.format(context),
                    style: const TextStyle(
                      fontSize: 48,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 24),

            // Label Input
            TextField(
              controller: _labelController,
              decoration: const InputDecoration(
                labelText: 'Label',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 24),

            // Repeat Days
            const Text(
              'Repeat',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: List.generate(7, (index) {
                return FilterChip(
                  label: Text(dayNames[index]),
                  selected: _repeatDays[index],
                  onSelected: (selected) {
                    setState(() {
                      _repeatDays[index] = selected;
                    });
                  },
                );
              }),
            ),
            const SizedBox(height: 24),

            // Ringtone Selection
            const Text(
              'Ringtone',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Card(
              child: ListTile(
                title: Text(_ringtoneManager.getRingtoneName(_selectedRingtoneIndex)),
                trailing: const Icon(Icons.chevron_right),
                onTap: _selectRingtone,
              ),
            ),
            const SizedBox(height: 32),

            // Action Buttons
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('Cancel'),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: _saveAlarm,
                  child: const Text('Save'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

