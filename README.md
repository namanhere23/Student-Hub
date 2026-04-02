# StudentHub

StudentHub is an Android application designed for IIIT Lucknow students to facilitate academic collaboration, document sharing, and campus communication.

## For Contribution
- Read [CONTRIBUTE.md](CONTRIBUTORS.md) file.

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
- **Firebase Cloud Messaging**: Push notifications
- **Firebase Crashlytics**: Crash reporting
- **Firebase Functions**: Serverless functions
- **Cloudinary**: Media storage and management

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
4. Set up Cloud Messaging and generate server key

### Firestore Security Rules
To ensure the application data and the admin panel are secure, you MUST deploy the following security rules to your Cloud Firestore database under the **Rules** tab in the Firebase Console:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // =====================================================================
    // HELPER FUNCTIONS (Entry Pass)
    // =====================================================================
    
    // Checks if the user is logged in AND has a valid college email
    function isIIITLUser() {
      return request.auth != null && request.auth.token.email.matches('.*@iiitl[.]ac[.]in$');
    }

    // Checks if the user passes the IIITL check AND is listed in the admin collection
    function isAdmin() {
      return isIIITLUser() && exists(/databases/$(database)/documents/admin/$(request.auth.token.email));
    }
    
    // =====================================================================
    // CORE COLLECTIONS
    // =====================================================================
    
    // --- ADMIN ---
    match /admin/{document=**} {
      allow read: if isIIITLUser();
      allow write: if isAdmin();
    }
    
    // --- OFFICIAL PAPERS (Approved) ---
    match /papers/{document=**} {
      // Any IIITL user can read/download the approved papers
      allow read: if isIIITLUser();
      // ONLY Admins can upload, edit, or delete official papers
      allow write: if isAdmin();
    }

    // --- PENDING PAPERS (Upload Queue) ---
    match /notConfirmedPapers/{document=**} {
      // IIITL users can see the pending list
      allow read: if isIIITLUser();
      
      // ANY logged-in person (even without IIITL email) can submit a new paper
      // (This blocks completely anonymous bots but allows outside contributions)
      allow create: if request.auth != null; 
      
      // Only admins can approve, modify, or delete pending papers
      allow update, delete: if isAdmin();
    }

    // --- USER PROFILES ---
    match /users/{userId} {
      allow read: if isIIITLUser();
      allow create: if isIIITLUser() && (request.auth.uid == userId || isAdmin());
      // Users can edit their own profile, but CANNOT change their email.
      allow update: if isIIITLUser() && (
        isAdmin() || 
        (request.auth.uid == userId && request.resource.data.email == resource.data.email)
      );
      allow delete: if isAdmin();
    }

    // --- GROUPS ---
    match /groups/{groupId} {
      allow read: if isIIITLUser();
      allow write: if isAdmin();
    }

    // --- PUSH NOTIFICATION TOKENS ---
    match /userTokens/{userId} {
      allow read: if isIIITLUser();
      allow write: if isIIITLUser() && request.auth.uid == userId;
    }

    // =====================================================================
    // CHAT & MESSAGING SYSTEM
    // =====================================================================

    // --- RECENT CHATS (Inbox Timestamps) ---
    match /usersChats/{userId}/partners/{partnerId} {
      allow read: if request.auth.uid == userId;
      allow write: if isIIITLUser() && (
        request.auth.uid == userId || 
        request.auth.uid == partnerId || 
        exists(/databases/$(database)/documents/groups/$(userId))
      );
    }

    // --- SECURED CHAT MESSAGES ---
    match /chats/{roomId}/messages/{messageId} {
      allow read, write: if isIIITLUser() && (
        roomId.matches('.*' + request.auth.uid + '.*') ||
        exists(/databases/$(database)/documents/groups/$(roomId))
      );
    }

    // =====================================================================
    // DEFAULT SAFETY NET
    // =====================================================================
    // Closes the database completely for any paths not explicitly matched above.
    match /{document=**} {
      allow read, write: if false; 
    }
    
  }
}
```

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

