# Local Android Studio Build Instructions

## ❗ Important Note
यह project **online GitHub Actions में build नहीं होगा** क्योंकि FFmpeg और YouTube-DL libraries Maven Central/JitPack से remove हो गई हैं।

**लेकिन Android Studio में locally build होगा!**

## 📦 Required Libraries (Manual Setup)

### Option 1: Use JitPack Cache (If Available)
अगर पहले कभी download किया था तो चल जाएगा automatically।

### Option 2: Download AAR Files Manually

#### Mobile-FFmpeg (4.4)
1. Download from: https://github.com/tanersener/mobile-ffmpeg/releases/tag/v4.4
2. File: `mobile-ffmpeg-min-4.4.aar`
3. Copy to: `app/libs/mobile-ffmpeg-min-4.4.aar`

#### YouTube-DL Android (0.14.0)
1. Download from GitHub releases या cached version use करो
2. Files needed:
   - `library-0.14.0.aar`
   - `ffmpeg-0.14.0.aar`
3. Copy to `app/libs/` folder

### Option 3: Update build.gradle.kts to use local libs

```kotlin
dependencies {
    // Use local AAR files
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
}
```

## 🔨 Build Steps

1. **Open in Android Studio**
   ```
   File > Open > Select android folder
   ```

2. **Sync Gradle**
   - Let Gradle sync complete
   - Download करेगा जो भी available है

3. **Build APK**
   ```
   Build > Build Bundle(s) / APK(s) > Build APK(s)
   ```

4. **Install on Device**
   ```
   Run > Run 'app'
   ```

## ✅ Working Configuration

| Component | Version | Status |
|-----------|---------|--------|
| Kotlin | 2.0.21 | ✅ Working |
| Compose Compiler | 2.0.21 | ✅ Working |
| AGP | 8.13.2 | ✅ Working |
| Gradle | 8.13 | ✅ Working |
| JDK | 21 | ✅ Recommended |

## 🚫 Why GitHub Actions Fails

- **Mobile-FFmpeg:** Removed from Maven Central (Jan 2025)
- **YouTube-DL Android:** Removed from JitPack (2025)

**Local build works** क्योंकि:
1. Gradle cache में पुराने versions हो सकते हैं
2. Manual AAR files add कर सकते हो
3. Alternative repositories use कर सकते हो

## 💡 Future Migration Plan

**Replace with:**
1. **FFmpeg → Media3 Transformer** (Google's official library)
2. **YouTube-DL → yt-dlp** या web-based solution

---

**Android Studio में build करो, APK मिल जाएगा!** 🎉
