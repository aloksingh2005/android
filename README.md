# Video Editor Android App

Professional video editing app for Android with Instagram scheduling.

## Features

- 📹 Upload videos from gallery
- 🎬 Download videos from YouTube
- ✂️ Trim videos with precise time selection
- 📐 Convert to 6 aspect ratios (9:16, 16:9, 1:1, 4:5, 4:3, 21:9)
- ⚙️ FFmpeg-powered video processing
- 📅 Schedule posts for Instagram
- 🔔 Automatic notifications
- 🎨 Modern Material Design 3 UI
- 🌙 Dark mode support

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM
- **Video Processing**: FFmpeg Kit
- **Video Playback**: ExoPlayer (Media3)
- **Database**: Room
- **Background Tasks**: WorkManager
- **UI**: Material Design 3

## Build

This project uses GitHub Actions for automatic APK building.

### Download APK

1. Go to [Actions](../../actions) tab
2. Click on the latest successful build
3. Download the `app-debug` artifact
4. Extract and install APK

### Build Locally (Optional)

Requires:
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 24-34

```bash
./gradlew assembleDebug
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

## Screenshots

Coming soon...

## License

Personal project for educational purposes.

**Note**: YouTube download feature is for personal use only. Do not distribute publicly.

## Author

Built with ❤️ using Kotlin
