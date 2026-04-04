# Welcome to StudentHub! (Contributors & Setup Guide)

---

## Architecture Overview
StudentHub is divided into two main repositories:
1. **Frontend (Android)**: This repository (Kotlin, Jetpack Compose, XML).
2. **[Backend](https://github.com/namanhere23/Student_Hub_Backend)**: Handles push notifications (FCM), Cloudinary media management, and custom APIs.

---

## Complete Step-by-Step Setup Guide

Follow these steps exactly to avoid any crashes or "Missing Configuration" errors.

### Prerequisites
Before you begin, ensure you have the following installed:
- **Android Studio**
- **Git** installed on your system
- A **Firebase Account**

### Step 1: Clone the Repositories
First, get the code on your local machine.

```bash
# Fork the Android Frontend and then clone it in base folder
git clone https://github.com/<your_username>/Student-Hub.git
cd Student-Hub

# Fork the Backend Server and then clone it in base folder
git clone https://github.com/<your_username>/Student_Hub_Backend.git
```

Open your Android project on Android Studio

### Step 2: Firebase Project Setup
StudentHub relies heavily on Firebase. You **MUST** set up your own Firebase project to run it locally.

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Click on **Add App** then select the **Android icon** to add an Android app to the project.
3. **Package Name:** Enter `com.namangulati.studenthub`.
4. **App Nickname:** `StudentHub`.
   - Click **Next** skipping the other optional setups.
   - Click **Continue to console**.
5. **Add SHA Keys:**
   - In the Firebase Console, go to `Project settings` > `General` > `Your apps`.
   - Click on `Add fingerprint` and add your SHA-1 and SHA-256 keys. You can find these keys in Android Studio:
     - Go to the terminal in android studio and run this command:
       ```bash
       ./gradlew signingreport
       ```
   - If it shows any error it means that your jdk is not properly set up.
     Watch this to set up JDK properly [Tutorial Video](https://youtu.be/R6MoDMASwag?si=_RAOa6wl3-YyChOq).
      - Copy the SHA-1 and SHA-256 keys from the report and add them to Firebase.
      - After adding the keys, download the `google-services.json` file provided and place it in the `app` directory of your project (select `Project` view in Android Studio to see the proper file structure).

### Step 3: Enable Firebase Services
In your Firebase Console, make sure you enable and setup the following:

- **Enable Firebase Services:**
- **Authentication:**
    - Go to the `Authentication` section from the Drawer in the `Security` in the Firebase Console.
    - Click on `Get started`
    - Click on the `Sign-in method` tab and enable `Email/Password` and `Google` sign-in methods. Click on add new provider to add more than one services.
    - Use your own email for `Support email` and click on save.
    - Download `google-services.json` and place it in the `app` directory of your project with same name.
- **Firestore Database:** 
  - Go to the `Firestore` section from the Drawer in the `Database and Storage` in the Firebase Console.
  - Click on `Create database`
  - Select the `Location` as `asia-south2(Delhi)` and click next.
  - Select `Start in production mode` and follow the instructions.
  - Go to the **Rules** tab and paste the exact security rules and then Publish it

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

### Step 4: Configure the Android App (`Constants.kt`)
This is the most crucial part. You need to link your API keys and the deployed backend URLs dynamically.

- GEMINI API KEY
    - Go to [Google  AI Studio](https://aistudio.google.com/)
    - Click on Get API key 
    - Create API Key
        - Give Name to your key
        - And choose the Student Hub (Or name given to your project in firebase console)
        - Create Key
    - Copy the API Key

- Backend Deployment
    - Go to [Student Hub Backend](https://github.com/namanhere23/Student_Hub_Backend)
    - Go to the README.md and deploy both backend as per the steps given there

Navigate to `app/src/main/java/com/namangulati/studenthub/API/Constants.kt` and update the dummy values with your actual keys and deployed service URLs.

```kotlin
package com.namangulati.studenthub.API

object Constants {
    // 1. Get this from Google AI Studio for the Chatbot functionality
    const val API_KEY_GEMINI = "YOUR_GEMINI_API_KEY_HERE"
    
    // 2. Base URL for your primary REST backend
    const val BASE_URL = "https://your-deployed-backend-url.onrender.com"
    
    // 3. Your StudentHub Backend URL for FCM Notifications & Media
    const val FCM_BASE_URL = "https://your-deployed-fcm-backend-url.onrender.com" 
}
```

### Step 5: Sync and Run the App
1. Open Android Studio.
2. Select **File > Open** and choose your cloned `Student-Hub` directory.
3. Let Gradle sync completely.
4. Select a physical device or emulator and click the **Run (▶)** button!

---

## 🤝 How to Contribute Code
Once you have everything set up and running, you're ready to contribute!

1. **Pick an Issue:** Find an open issue or suggest a new feature.
2. **Create a Branch:** Create a branch based on what you are working on.
   - Example: `git checkout -b feature/login-screen` or `git checkout -b bugfix/chat-crash`
3. **Make Changes:** Write clean Kotlin code and respect the MVVM architecture and Jetpack Compose standard practices.
4. **Commit:** Use [Conventional Commits](https://www.conventionalcommits.org/).
   - Example: `git commit -m "feat: added offline caching for papers"` or `git commit -m "fix: resolved crash on detail screen"`
5. **Push:** `git push origin your-branch-name`
6. **Pull Request:** Open a PR describing exactly what you fixed or created. Attach screenshots if you made UI changes!

> **Note:** If your changes require updates to `Constants.kt` or `.env` files, **DO NOT** commit your actual API keys. Always use placeholders!


Thank you for helping us improve StudentHub! We look forward to your PRs.