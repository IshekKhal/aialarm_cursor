import 'package:flutter/material.dart';
import '../services/ringtone_manager.dart';

class RingtoneSelectionScreen extends StatefulWidget {
  final int selectedIndex;

  const RingtoneSelectionScreen({super.key, required this.selectedIndex});

  @override
  State<RingtoneSelectionScreen> createState() => _RingtoneSelectionScreenState();
}

class _RingtoneSelectionScreenState extends State<RingtoneSelectionScreen> {
  late int _selectedIndex;
  final RingtoneManager _ringtoneManager = RingtoneManager.instance;

  @override
  void initState() {
    super.initState();
    _selectedIndex = widget.selectedIndex;
  }

  @override
  void dispose() {
    _ringtoneManager.stopPreview();
    super.dispose();
  }

  Future<void> _previewRingtone(int index) async {
    await _ringtoneManager.previewRingtone(index);
  }

  void _selectRingtone(int index) {
    _ringtoneManager.stopPreview();
    setState(() {
      _selectedIndex = index;
    });
    Navigator.pop(context, index);
  }

  @override
  Widget build(BuildContext context) {
    final ringtones = RingtoneManager.getAvailableRingtones();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Select Ringtone'),
      ),
      body: ListView.builder(
        itemCount: ringtones.length,
        itemBuilder: (context, index) {
          final ringtone = ringtones[index];
          final isSelected = index == _selectedIndex;

          return Card(
            margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
            child: ListTile(
              title: Text(ringtone.name),
              trailing: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextButton(
                    onPressed: () => _previewRingtone(index),
                    child: const Text('Preview'),
                  ),
                  if (isSelected)
                    const Icon(
                      Icons.check,
                      color: Colors.green,
                    ),
                ],
              ),
              onTap: () => _selectRingtone(index),
            ),
          );
        },
      ),
    );
  }
}

