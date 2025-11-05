import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';
import '../models/alarm.dart';

class AlarmDatabase {
  static final AlarmDatabase instance = AlarmDatabase._init();
  static Database? _database;

  AlarmDatabase._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('alarms.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(
      path,
      version: 2,
      onCreate: _createDB,
      onUpgrade: _onUpgrade,
    );
  }

  Future<void> _createDB(Database db, int version) async {
    await db.execute('''
      CREATE TABLE alarms (
        id INTEGER PRIMARY KEY,
        hour INTEGER,
        minute INTEGER,
        label TEXT,
        enabled INTEGER,
        repeat_days TEXT,
        vibrate INTEGER,
        ringtone_index INTEGER
      )
    ''');
  }

  Future<void> _onUpgrade(Database db, int oldVersion, int newVersion) async {
    if (oldVersion < 2) {
      // Migration from old version if needed
      await db.execute('''
        CREATE TABLE IF NOT EXISTS alarms_new (
          id INTEGER PRIMARY KEY,
          hour INTEGER,
          minute INTEGER,
          label TEXT,
          enabled INTEGER,
          repeat_days TEXT,
          vibrate INTEGER,
          ringtone_index INTEGER
        )
      ''');
      
      await db.execute('''
        INSERT INTO alarms_new 
        SELECT id, hour, minute, label, enabled, repeat_days, vibrate, 0
        FROM alarms
      ''');
      
      await db.execute('DROP TABLE alarms');
      await db.execute('ALTER TABLE alarms_new RENAME TO alarms');
    }
  }

  Future<void> addAlarm(Alarm alarm) async {
    final db = await database;
    await db.insert('alarms', alarm.toMap(), conflictAlgorithm: ConflictAlgorithm.replace);
  }

  Future<void> updateAlarm(Alarm alarm) async {
    final db = await database;
    await db.update(
      'alarms',
      alarm.toMap(),
      where: 'id = ?',
      whereArgs: [alarm.id],
    );
  }

  Future<void> deleteAlarm(int id) async {
    final db = await database;
    await db.delete(
      'alarms',
      where: 'id = ?',
      whereArgs: [id],
    );
  }

  Future<Alarm?> getAlarm(int id) async {
    final db = await database;
    final maps = await db.query(
      'alarms',
      where: 'id = ?',
      whereArgs: [id],
    );

    if (maps.isNotEmpty) {
      return Alarm.fromMap(maps.first);
    }
    return null;
  }

  Future<List<Alarm>> getAllAlarms() async {
    final db = await database;
    final maps = await db.query(
      'alarms',
      orderBy: 'hour, minute',
    );

    return maps.map((map) => Alarm.fromMap(map)).toList();
  }

  Future<void> close() async {
    final db = await database;
    await db.close();
  }
}

