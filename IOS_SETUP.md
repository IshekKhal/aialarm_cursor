# iOS Setup for ALARMv1 (Flutter)

This app supports iOS with local notifications, vibration, and bundled ringtones. Follow these steps to finish iOS integration.

## 1) Requirements
- Xcode (latest)
- CocoaPods (installed via `sudo gem install cocoapods` or Xcode)
- iOS device or simulator (note: notifications behave differently on simulator)

## 2) Open the iOS project
```
open ios/Runner.xcworkspace
```
If the `ios/` folder is missing, run `flutter create .` in the project root to scaffold iOS.

## 3) Enable Push/Notification capability
In Xcode:
- Select the `Runner` target → `Signing & Capabilities`
- Add `Push Notifications` (enables local notification entitlement)

## 4) Add 10 bundled notification sounds
iOS requires notification sounds to be in the app bundle (not Flutter assets) and be one of: `.caf`, `.aiff`, `.wav`. Recommended: `.caf` (< 30 seconds each).

1. Prepare your 10 audio files as:
   - `ringtone_1.caf`
   - `ringtone_2.caf`
   - ...
   - `ringtone_10.caf`

2. In Xcode, right-click `Runner` → `Add Files to "Runner"...`
3. Select the 10 files and ensure options:
   - `Copy items if needed` → checked
   - `Add to targets` → `Runner` checked
4. Verify they appear under the `Runner` target and are included in `Build Phases` → `Copy Bundle Resources`.

The app references these names when scheduling notifications on iOS.

## 5) Info.plist updates (if needed)
Normally not required for local notifications on iOS 10+, but confirm these keys exist in `ios/Runner/Info.plist` if you need foreground presentation:
- `UIBackgroundModes` with item `audio` (only if you plan continuous background audio beyond notification)

Local notifications do not require additional plist keys. `flutter_local_notifications` handles permission prompts at runtime.

## 6) Pod install
In the project root:
```
cd ios
pod install
cd ..
```

## 7) Build & run
```
flutter pub get
flutter run
```
Select an iOS device/simulator. On first run, iOS will prompt for notification permissions.

## 8) How this app uses iOS features
- Requests notification permissions at startup (alerts, sounds, badges)
- Schedules local notifications using `flutter_local_notifications`
- Plays the selected bundled sound (`ringtone_#.caf`) as the notification sound
- Full-screen intent is not available on iOS; users tap the notification to open the app
- The in-app ringing screen plays the Flutter asset audio (mp3) in a loop and vibrates the device

## 9) Troubleshooting
- No sound on notification:
  - Ensure `.caf` files are present in the Runner bundle and under `Copy Bundle Resources`
  - File names must match exactly: `ringtone_1.caf` ... `ringtone_10.caf`
  - Sound duration must be short (< 30s)
- Permission prompt not shown:
  - Uninstall the app from the device and reinstall
  - Check iOS Settings → Your App → Notifications
- Build errors with pods:
  - `cd ios && pod repo update && pod install`
  - `flutter clean && flutter pub get && flutter run`
