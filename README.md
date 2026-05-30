# 🕌 Tafheem Bangla Quran - তাফহীমুল কুরআন

[![Version](https://img.shields.io/badge/Version-27-blue.svg)](https://play.google.com/store/apps/details?id=com.minbar.tafhimulquran)
[![Android](https://img.shields.io/badge/Android-5.0%2B-green.svg)](https://developer.android.com/studio)
[![License](https://img.shields.io/badge/License-Free-orange.svg)](https://github.com/minbardev/TafheemBangla/blob/master/LICENSE)

<p align="center">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80">
</p>

## 📖 Overview

Tafheem Bangla Quran is a comprehensive Android application that provides the complete Quran with Tafheem-ul-Quran translation by Syed Abul A'la Maududi in Bengali. This app aims to make the divine wisdom of the Quran easily accessible to Bengali speakers worldwide, featuring multiple translations, tafsirs, prayer times, and educational tools.

## ✨ Key Features

### 📚 Quran Reading & Study
- **Complete Quran Text**: Arabic text with color-coded Tajweed rules
- **Multiple Tafsir Options**:
  - Tafheemul Quran by Syed Abu Ala Maududi (Bangla)
  - Tafsir Ibn Kathir (Bangla)
  - Fi Zilalil Quran by Sayyid Qutb (Bangla)
  - English translations available
- **Bengali Translation**: Easy-to-understand Bengali translation
- **Advanced Search**: Search by keywords, surah names, or themes
- **Bookmarking**: Save and organize favorite verses
- **Reading History**: Track last read position with resume functionality

### 🕌 Prayer & Islamic Tools
- **Global Prayer Times** (Recently Enhanced):
  - Works worldwide with proper timezone support
  - Multiple calculation methods (7 different methods)
  - Support for different Islamic schools (Hanafi, Shafi'i, Maliki, Hanbali)
  - Automatic location detection or manual city selection
  - Real-time countdown to next prayer
  - Visual progress bar between prayers
- **Azan Notifications**:
  - Multiple Azan sounds to choose from
  - Customizable alerts with vibration
  - Exact alarm support for timely notifications
  - Test Azan feature for troubleshooting
- **Hadith Collection**: Complete Sahih Bukhari with chapter organization

### 🎨 User Experience
- **Modern UI**: Material Design 3 with smooth animations
- **Theme Support**: Light, dark, and custom themes
- **Font Customization**: 
  - Adjustable Arabic and Bengali font sizes
  - Multiple font family options
- **Offline Support**: Complete functionality without internet
- **Responsive Design**: Optimized for various screen sizes

### 📱 Additional Features
- **Daily Quran & Hadith**: Daily verses and hadiths for reflection
- **Audio Support**: Quran recitation (in development)
- **Share Functionality**: Share verses on social media
- **Copy to Clipboard**: Copy text for notes
- **Home Screen Widget**: Quick prayer times on home screen
- **Navigation Drawer**: Easy access to all features

## 🚀 Recent Updates (Version 27)

### Major Improvements:
1. **Fixed Global Prayer Time Calculation**: 
   - Resolved timezone issues for international users
   - Added support for countries outside Bangladesh
   - Enhanced location selection with country picker

2. **Enhanced Prayer Features**:
   - Added exact alarm permission handling for Android 12+
   - Implemented full-screen intent support for Android 14+
   - Added battery optimization handling

3. **UI Improvements**:
   - Updated prayer time cards with modern design
   - Enhanced location display with country support
   - Improved real-time prayer countdown accuracy

4. **Performance Optimizations**:
   - Optimized database queries
   - Improved memory usage
   - Enhanced background task management

## 🛠 Technical Specifications

- **Platform**: Android 5.0 (Lollipop) and above
- **Architecture**: MVVM with Fragment-based navigation
- **Database**: SQLite for local storage
- **Networking**: Volley API for prayer time data
- **Background Tasks**: Android WorkManager for periodic updates
- **Build System**: Gradle with Java 8
- **Target SDK**: 35 (Android 14)
- **Min SDK**: 21 (Android 5.0)

## 📁 Project Structure

```
app/
├── src/main/java/com/minbar/tafhimulquran/
│   ├── Activity/          # Main activities
│   ├── Adapter/           # RecyclerView adapters
│   ├── Daily/            # Daily Quran & Hadith features
│   ├── Hadith/           # Hadith collection
│   ├── Model/            # Data models
│   ├── Prayer/           # Prayer times and notifications
│   ├── Utils/            # Utility classes
│   └── Fragment/         # UI fragments
├── src/main/res/         # Resources (layouts, drawables, etc.)
└── gradle/              # Gradle wrapper files
```

## 🏗 Build Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or later
- Android SDK API 35
- Android Build Tools 34.0.0+

### Building the Project
1. Clone the repository:
   ```bash
   git clone https://github.com/minbardev/TafheemBangla.git
   cd TafheemBangla
   ```

2. Open in Android Studio:
   - Open `app/build.gradle` as a project
   - Sync Gradle dependencies

3. Build and run:
   - Connect an Android device or start an emulator
   - Click the green 'Run' button or press `Shift + F10`

### Alternative Command Line Build
```bash
./gradlew assembleDebug
```

## 🔒 Permissions Required

- `INTERNET`: For fetching prayer times and additional content
- `ACCESS_FINE_LOCATION`: For automatic prayer location detection
- `WRITE_EXTERNAL_STORAGE` / `READ_EXTERNAL_STORAGE`: For audio and data files
- `SCHEDULE_EXACT_ALARM`: For timely prayer notifications
- `POST_NOTIFICATIONS`: For displaying prayer alerts
- `RECEIVE_BOOT_COMPLETED`: For restarting alarms after device reboot
- `USE_FULL_SCREEN_INTENT`: For full-screen Azan notifications (Android 14+)
- `WAKE_LOCK`: To keep device awake for notifications
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: For reliable alarm delivery

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Translation Contribution Guidelines
- Ensure translations are accurate and maintain the original meaning
- Follow Bengali linguistic conventions
- Preserve the respectful tone of Islamic content

## 📞 Support & Feedback

- **Report Issues**: [GitHub Issues](https://github.com/minbardev/TafheemBangla/issues)
- **Feature Requests**: Use the GitHub Issues page
- **Email Support**: Available through the app's contact option
- **Community**: Join our user community for discussions and help

## 🌐 Download Links

- [Google Play Store](https://play.google.com/store/apps/details?id=com.minbar.tafhimulquran)
- [APK Download](https://github.com/minbardev/TafheemBangla/releases) (Latest builds)

## 🙏 Special Thanks

- Syed Abul A'la Maududi for his invaluable Tafheem-ul-Quran
- All contributors who have helped improve this application
- The open-source community for their libraries and tools

## 📊 Privacy Policy

The app respects your privacy:
- No personal data is collected
- Location data is only used for prayer time calculation
- All preferences are stored locally on your device
- No analytics or tracking libraries are integrated

---

Made with ❤️ for the Ummah by [XovenTech](https://play.google.com/store/apps/dev?id=6812471568616278487)
