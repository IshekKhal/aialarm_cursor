import 'package:audioplayers/audioplayers.dart';

class RingtoneInfo {
  final int index;
  final String name;
  final String assetPath;

  RingtoneInfo({
    required this.index,
    required this.name,
    required this.assetPath,
  });
}

class RingtoneManager {
  static final RingtoneManager instance = RingtoneManager._init();
  RingtoneManager._init();

  final AudioPlayer _previewPlayer = AudioPlayer();
  
  static const List<String> ringtoneNames = [
    'Classic Alarm',
    'Morning Breeze',
    'Digital Wake',
    'Gentle Chime',
    'Nature Sounds',
    'Electronic Pulse',
    'Soft Melody',
    'Rising Sun',
    'Peaceful Dawn',
    'Energize',
  ];

  static List<RingtoneInfo> getAvailableRingtones() {
    return List.generate(10, (index) {
      return RingtoneInfo(
        index: index,
        name: ringtoneNames[index],
        assetPath: 'assets/ringtones/ringtone_${index + 1}.mp3',
      );
    });
  }

  String getRingtoneAssetPath(int ringtoneIndex) {
    if (ringtoneIndex < 0 || ringtoneIndex >= 10) {
      return 'assets/ringtones/ringtone_1.mp3';
    }
    return 'assets/ringtones/ringtone_${ringtoneIndex + 1}.mp3';
  }

  String getRingtoneName(int ringtoneIndex) {
    if (ringtoneIndex < 0 || ringtoneIndex >= ringtoneNames.length) {
      return 'Default';
    }
    return ringtoneNames[ringtoneIndex];
  }

  // iOS notification sounds must be part of the iOS app bundle (not Flutter assets)
  // and must use supported formats like .caf/.aiff/.wav, typically < 30s.
  // We assume files named: ringtone_1.caf ... ringtone_10.caf added to Runner target.
  DarwinNotificationSound getIosNotificationSound(int ringtoneIndex) {
    final idx = (ringtoneIndex >= 0 && ringtoneIndex < 10) ? ringtoneIndex : 0;
    final name = 'ringtone_${idx + 1}.caf';
    return DarwinNotificationSound(name);
  }

  Future<void> previewRingtone(int ringtoneIndex) async {
    await stopPreview();
    final assetPath = getRingtoneAssetPath(ringtoneIndex);
    await _previewPlayer.play(AssetSource(assetPath.replaceFirst('assets/', '')));
  }

  Future<void> stopPreview() async {
    await _previewPlayer.stop();
  }

  Future<void> dispose() async {
    await _previewPlayer.dispose();
  }
}

