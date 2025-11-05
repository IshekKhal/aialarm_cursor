# Flutter Setup Instructions

## Step 1: Install Flutter

### Windows Installation

1. **Download Flutter SDK:**
   - Visit: https://docs.flutter.dev/get-started/install/windows
   - Download the latest Flutter SDK zip file
   - Extract to a location like `C:\src\flutter` (avoid paths with spaces)

2. **Add Flutter to PATH:**
   - Open System Environment Variables
   - Edit the "Path" variable
   - Add: `C:\src\flutter\bin` (or your Flutter installation path)

3. **Verify Installation:**
   ```bash
   flutter doctor
   ```

4. **Install Required Tools:**
   - Install Android Studio (if not already installed)
   - Install Android SDK
   - Accept Android licenses:
     ```bash
     flutter doctor --android-licenses
     ```

## Step 2: Install Project Dependencies

Once Flutter is installed and in your PATH:

1. **Navigate to project directory:**
   ```bash
   cd C:\Users\AbhishekKhanra\ALARMv1
   ```

2. **Install dependencies:**
   ```bash
   flutter pub get
   ```

This will install all packages listed in `pubspec.yaml`:
- `sqflite` - SQLite database
- `flutter_local_notifications` - Alarm notifications
- `timezone` - Timezone handling
- `audioplayers` - Audio playback
- `vibration` - Vibration control
- `intl` - Internationalization
- And other dependencies

## Step 3: Verify Installation

Check if everything is set up correctly:

```bash
flutter doctor
```

This will show:
- ✅ Flutter SDK installed
- ✅ Android toolchain installed
- ✅ Android Studio installed
- ⚠️ Any missing dependencies

## Step 4: Run the App

Once dependencies are installed:

```bash
flutter run
```

Or use Android Studio:
1. Open Android Studio
2. File → Open → Select the project folder
3. Wait for Flutter to sync
4. Click Run button

## Troubleshooting

### Flutter not found
- Make sure Flutter is added to PATH
- Restart terminal/command prompt
- Verify installation: `flutter --version`

### Dependencies not installing
- Check internet connection
- Try: `flutter clean` then `flutter pub get`
- Check if you're in the correct directory

### Android SDK issues
- Install Android Studio
- Open Android Studio → SDK Manager
- Install Android SDK Platform 34
- Install Android SDK Build-Tools

## Quick Setup (Using Android Studio)

1. Open Android Studio
2. File → New → New Flutter Project
3. Or: File → Open → Select existing project
4. Android Studio will automatically run `flutter pub get`
5. Wait for Gradle sync to complete

## Alternative: Use Android Studio Terminal

If Flutter is not in system PATH, you can use Android Studio's terminal:
1. Open Android Studio
2. View → Tool Windows → Terminal
3. Run: `flutter pub get`

