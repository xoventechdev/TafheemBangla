# Tafheem Bangla Quran - Project Documentation

## Project Summary & Active Features

Tafheem Bangla Quran is an Android application for reading and studying the Quran with Tafheem-ul-Quran translation in Bangla. The app provides a comprehensive Quranic reading experience with features for daily study, prayer times, hadith collection, and verse memorization.

### Active Features:
- **Quran Reader**: Full Quran text with Tafheem-ul-Quran Bangla translation
- **Verse Navigation**: Search and navigate to specific verses
- **Favorites**: Bookmark and save favorite verses
- **Daily Activity**: Daily Quran reading tracker and reminders
- **Prayer Times**: Accurate prayer time calculations and notifications
- **Hadith Collection**: Access to hadith chapters and content
- **Settings**: Customizable app preferences and themes
- **Audio Support**: Audio recitation for verses (in development)
- **Fragment-based UI**: Modern Android UI using Fragments for better navigation

## Tech Stack

- **Platform**: Android (Native)
- **Language**: Java
- **Build System**: Gradle
- **Architecture**: MVVM with Activity-based UI and Fragment support
- **Storage**: Local SQLite database for Quran data
- **Dependencies**: AndroidX, Material Components, various utility libraries

## Code Style & Naming Conventions

### Package Structure:
- `com.minbar.tafhimulquran.Activity` - All Activity classes
- `com.minbar.tafhimulquran.Adapter` - RecyclerView adapters
- `com.minbar.tafhimulquran.Daily` - Daily activity features
- `com.minbar.tafhimulquran.Hadith` - Hadith-related components
- `com.minbar.tafhimulquran.Model` - Data models and database entities
- `com.minbar.tafhimulquran.Prayer` - Prayer functionality
- `com.minbar.tafhimulquran.Utils` - Utility classes
- `com.minbar.tafhimulquran.Fragment` - UI fragments

### Naming Conventions:
- **Classes**: PascalCase (e.g., `MainActivity`, `VerseActivity`)
- **Methods**: camelCase (e.g., `onCreate`, `loadData`)
- **Variables**: camelCase (e.g., `verseList`, `adapter`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `REQUEST_CODE_PERMISSION`)
- **XML Resources**: snake_case (e.g., `activity_main.xml`, `item_verse.xml`)

### Code Patterns:
- Each Activity follows standard Android lifecycle methods
- Adapters use ViewHolder pattern for RecyclerView
- Database operations handled in separate utility classes
- Theme management centralized in ThemeManager
- Fragment transactions managed through FragmentManager

## Known Bugs & Next TODOs

### Known Issues:
- None currently identified

### Next Development Tasks:
1. **Enhanced Search**: Improve search functionality with fuzzy matching
2. **Audio Support**: Add audio recitation for verses
3. **Bookmark Management**: Better organization of favorite verses
4. **Offline Mode**: Complete offline functionality with cached data
5. **Performance Optimization**: Improve app startup time and memory usage
6. **UI/UX Improvements**: Enhance fragment transitions and navigation

## Test Scenarios

### Completed Test Cases:
- [x] Basic Quran reading functionality
- [x] Verse navigation and search
- [x] Favorites bookmarking
- [x] Daily activity tracking
- [x] Prayer time calculations
- [x] Hadith chapter navigation
- [x] Settings configuration
- [x] Theme switching
- [x] Fragment navigation

### Pending Test Cases:
- [ ] Audio playback functionality
- [ ] Offline mode behavior
- [ ] Push notification handling
- [ ] Background service stability
- [ ] Memory usage under heavy Quran text load
- [ ] Deep linking and app shortcuts

## Build & Development

### Build Configuration:
- Gradle wrapper version: 8.0.2
- Target SDK: 34
- Minimum SDK: 21
- Build flavors: Not currently configured

### Development Workflow:
1. Make changes to Java source files in `app/src/main/java`
2. Update XML layouts in `app/src/main/res/layout`
3. Modify strings in `app/src/main/res/values/strings.xml`
4. Update colors/themes in respective resource files
5. Build and test using Android Studio or command line

### Testing Commands:
```bash
# Build the project
gradle build

# Run tests
gradle test

# Install on connected device
gradle installDebug
```

## File Structure Overview

```
app/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/minbar/tafhimulquran/
│       │       ├── Activity/          # Main Activity classes
│       │       ├── Adapter/           # RecyclerView adapters
│       │       ├── Daily/             # Daily activity features
│       │       ├── Hadith/            # Hadith components
│       │       ├── Model/             # Data models and database entities
│       │       ├── Prayer/            # Prayer functionality
│       │       ├── Utils/             # Utility classes
│       │       └── Fragment/          # UI fragments
│       └── res/
│           ├── layout/              # XML layout files
│           ├── values/              # String and color resources
│           └── xml/                 # Preference and config files
├── build.gradle              # App build configuration
├── gradle/                   # Gradle wrapper
└── CLAUDE.md                 # This documentation file
```

## Theme Management

The app uses a centralized theme system:
- Theme switching handled by `ThemeManager` utility class
- Color resources defined in `colors.xml`
- Theme styles in `themes.xml`
- Default theme: Light theme with customizable colors

## Database & Data Management

- Quran data stored locally in SQLite database
- Data models in `com.minbar.tafhimulquran.Model` package
- Database operations through utility classes
- Data loading handled in background threads

## Recent Changes & Updates

- Added theme management system
- Enhanced search functionality
- Improved prayer time calculations
- Better notification handling
- Optimized memory usage for large text content
- Introduced Fragment-based UI for better navigation
- Added audio player foundation (in progress)

## Important Notes

- Always test on actual Android devices, not just emulators
- Consider Quran text size when optimizing memory usage
- Prayer times should be accurate for Bangladesh location by default
- User data (favorites, settings) should be preserved during updates
- Follow Android best practices for background services and notifications
- Handle fragment transactions carefully to avoid memory leaks

## Version History

- **v27**: Current stable version with enhanced theme support
- **v26**: Added improved search and prayer notifications
- **v25**: Initial release with core Quran reading functionality

---
*This documentation is maintained to help developers quickly understand and contribute to the Tafheem Bangla Quran project.*