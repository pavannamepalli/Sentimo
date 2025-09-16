# Sentimo - Emotion Detection App

A real-time emotion detection Android application that uses Google ML Kit to analyze facial expressions and detect emotions through the device camera.

## Features

- **Real-time Emotion Detection**: Live analysis of facial expressions
- **Eye Tracking**: Monitors left and right eye openness
- **Smile Detection**: Measures smile probability
- **Visual Feedback**: Color-coded emotion display with emojis
- **Clean UI**: Modern, intuitive interface with overlay information
- **Performance Optimized**: Efficient camera and ML processing

## Screenshots

The app displays real-time emotion analysis with:
- Current emotion type (Happy, Smiling, Neutral, Sleepy)
- Smile probability percentage
- Left and right eye openness percentages
- Visual indicators (👁️ for open eyes, 😑 for closed eyes)
- Color-coded status (Green = Open, Red = Closed)

## Technical Architecture

### Project Structure
```
com.example.sentimo/
├── camera/
│   └── CameraManager.kt          # Camera operations and lifecycle
├── data/
│   └── model/
│       └── EmotionData.kt        # Data models for emotion detection
├── face/
│   └── FaceDetectionManager.kt   # ML Kit face detection logic
├── ui/
│   ├── components/
│   │   └── EmotionOverlayView.kt # Custom view for emotion display
│   └── viewmodel/
│       └── MainViewModel.kt      # ViewModel for UI state management
├── utils/
│   ├── Constants.kt              # App-wide constants
│   └── PermissionUtils.kt        # Permission handling utilities
└── MainActivity.kt               # Main activity with MVVM pattern
```

### Key Technologies
- **Android SDK**: Target SDK 35, Minimum SDK 24
- **Kotlin**: Modern Android development
- **CameraX**: Camera functionality
- **Google ML Kit**: Face detection and emotion analysis
- **Firebase**: Analytics integration
- **Material Design 3**: UI components
- **MVVM Architecture**: Clean separation of concerns
- **Coroutines**: Asynchronous programming

## Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Device with front-facing camera
- Internet connection for ML Kit model downloads

### Setup
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on device or emulator

### Permissions
The app requires the following permissions:
- `CAMERA` - For face detection and emotion analysis
- `INTERNET` - For ML Kit model downloads

## Usage

1. **Launch the App**: Open Sentimo on your device
2. **Grant Permissions**: Allow camera access when prompted
3. **Position Your Face**: Point the camera at your face
4. **View Results**: Watch real-time emotion analysis
5. **Test Features**: 
   - Close one eye to see eye detection
   - Smile to see smile probability changes
   - Observe emotion classification changes

## Emotion Types

The app detects and classifies the following emotions:

- **😊 Happy**: Smiling with both eyes open
- **😄 Smiling**: Smiling detected
- **😐 Neutral**: Default state
- **😴 Sleepy**: Both eyes closed
- **❓ Unknown**: Unclear emotion state

## Performance Features

- **Optimized Processing**: Fast face detection mode
- **Resource Management**: Proper camera lifecycle handling
- **Memory Efficient**: Automatic cleanup of resources
- **Battery Friendly**: Camera stops when app is paused


## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Future Enhancements

Potential features for future versions:
- More emotion types (anger, sadness, surprise)
- Emotion history tracking
- Export emotion data
- Settings and customization
- Multiple face detection

---

**Sentimo** - Understanding emotions through technology
