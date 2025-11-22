# StudentHub

StudentHub is an Android application designed for IIIT Lucknow students to facilitate academic collaboration, document sharing, and campus communication.

## Features

### 🔐 Authentication
- Email/Password authentication (restricted to `@iiitl.ac.in` domain)
- Google Sign-In integration
- Password reset functionality
- User profile management

### 📄 Paper Management
- Upload academic papers and documents
- Browse and view shared papers
- Admin approval system for paper submissions
- Offline support with Room database for cached papers

### 💬 Real-time Chat
- One-on-one messaging between users
- Group chat functionality
- Online/offline status indicators
- Media sharing support

### 🤖 AI Chatbot
- Gemini-powered AI assistant
- Campus-specific information queries
- Academic schedules, events, and notices
- Natural language conversation interface

### 🔔 Push Notifications
- Firebase Cloud Messaging integration
- Real-time notifications for messages and updates
- Topic-based subscriptions

### 👤 User Management
- User profiles with photo, name, and contact information
- Group memberships
- Contact list management

## Tech Stack

### Core Technologies
- **Language**: Kotlin
- **Minimum SDK**: 28 (Android 9.0)
- **Target SDK**: 35 (Android 15)
- **Build System**: Gradle with Kotlin DSL

### Architecture & UI
- **UI Framework**: Jetpack Compose + ViewBinding
- **Architecture Components**: ViewModel, LiveData
- **Navigation**: Custom navigation utilities

### Backend Services
- **Firebase Authentication**: User authentication
- **Firebase Firestore**: Document database
- **Firebase Realtime Database**: Real-time data synchronization
- **Firebase Cloud Messaging**: Push notifications
- **Firebase Crashlytics**: Crash reporting
- **Firebase Functions**: Serverless functions

### Local Storage
- **Room Database**: Local caching and offline support
- **Data Binding**: View binding enabled

### Networking
- **Retrofit**: REST API client
- **OkHttp**: HTTP client
- **Gson**: JSON serialization

### Image Loading
- **Glide**: Image loading and caching

### AI Integration
- **Google Generative AI**: Gemini 2.5 Flash model for chatbot

### Other Libraries
- **Material Design Components**: UI components
- **Splash Screen API**: App launch experience
- **Credentials API**: Google Sign-In integration

## Project Structure

```
app/src/main/java/com/namangulati/studenthub/
├── adapters/          # RecyclerView adapters
├── admin/             # Admin-specific features
├── API/               # API interfaces and utilities
├── controllers/       # ViewModels and app controllers
├── Dao/               # Room database DAOs
├── Database/          # Room database and repositories
├── fragments/         # UI fragments
├── models/            # Data models
├── modelsRoom/        # Room database entities
├── uiutils/           # UI utility classes
├── userPages/         # Main user-facing activities
└── utils/             # Utility classes
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 8 or higher
- Android SDK with API level 28+
- Firebase project with the following services enabled:
  - Authentication
  - Firestore
  - Realtime Database
  - Cloud Messaging
  - Crashlytics

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd StudentHub
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json` and place it in:
     - `app/google-services.json`
   - Enable the required Firebase services mentioned above

 3. **Add SHA Keys:**
 - In the Firebase Console, go to `Project settings` > `General` > `Your apps`.
 - Click on `Add fingerprint` and add your SHA-1 and SHA-256 keys. You can find these keys in Android Studio:
   - Go to the terminal in android studio and run this command:
     ```
     ./gradlew signingreport
 - If it shows any error it means that your jdk is not properly set up.
    - Copy the SHA-1 and SHA-256 keys from the report and add them to Firebase.
    - After adding SHA-1 and SHA-256 keys and then download the `google-services.json` file provided and place it in the `app` directory of your project.
      
4. **Configure API Keys**
   - Add your Gemini API key in `app/src/main/java/com/namangulati/studenthub/API/Constants.kt`
   - Add your Google Sign-In Web Client ID in `app/src/main/res/values/strings.xml` as `default_web_client_id`

5. **Build the project**
   ```
   ./gradlew build
   ```

6. **Run the app**
   - Connect an Android device or start an emulator
   - Run the app from Android Studio or use:
     ```
     ./gradlew installDebug
     ```

## Configuration

### Firebase Setup
1. Enable Email/Password authentication in Firebase Console
2. Configure OAuth consent screen for Google Sign-In
3. Set up Firestore database with appropriate security rules
4. Configure Realtime Database rules
5. Set up Cloud Messaging and generate server key

### Permissions
The app requires the following permissions:
- `INTERNET`: Network access
- `ACCESS_NETWORK_STATE`: Check network connectivity
- `POST_NOTIFICATIONS`: Push notifications (Android 13+)
- `WRITE_EXTERNAL_STORAGE`: File operations
- `ACCESS_FINE_LOCATION`: Location services (if needed)
- `ACCESS_COARSE_LOCATION`: Location services (if needed)

## Building

### Debug Build
```
./gradlew assembleDebug
```

### Release Build
```
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/`

## Dependencies

Key dependencies are managed in `gradle/libs.versions.toml`. Major dependencies include:

- AndroidX Core KTX
- Material Design Components
- Firebase BOM (33.5.1)
- Jetpack Compose
- Room Database
- Retrofit & OkHttp
- Glide
- Google Generative AI

## Version

- **Version Code**: 1
- **Version Name**: 1.0


## Authors

- **Naman Gulati**

---

**Note**: This app is specifically designed for IIIT Lucknow students and requires a valid `@iiitl.ac.in` email address for authentication.

