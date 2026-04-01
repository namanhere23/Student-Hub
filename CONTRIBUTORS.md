# Welcome to StudentHub! (Contributors & Setup Guide)

---

## Architecture Overview
StudentHub is divided into two main repositories:
1. **Frontend (Android)**: This repository (Kotlin, Jetpack Compose, XML).
2. **[Backend (Node.js/Express)](https://github.com/namanhere23/Student_Hub_Backend)**: Handles push notifications (FCM), Cloudinary media management, and custom APIs.

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
# Clone the Android Frontend
git clone https://github.com/namanhere23/Student-Hub.git
cd Student-Hub

# Clone the Backend Server
git clone https://github.com/namanhere23/Student_Hub_Backend.git
```

### Step 2: Firebase Project Setup
StudentHub relies heavily on Firebase. You **MUST** set up your own Firebase project to run it locally.

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Click on the **Android Icon** to add an Android app to the project.
3. **Package Name:** Enter `com.namangulati.studenthub`.
4. **App Nickname:** StudentHub.
5. **SHA-1 & SHA-256 Certificates:** 
   - Open the terminal in Android Studio and run `./gradlew signingreport`.
   - Copy the SHA-1 and SHA-256 keys for the `debug` variant and paste them into Firebase. *(This is mandatory for Google Sign-In!)*
6. **Download `google-services.json`:**
   - Download the file and move it into the `app/` directory of your cloned Android project.

### Step 3: Enable Firebase Services
In your Firebase Console, make sure you enable and setup the following:

- **Authentication:** Enable both **Email/Password** and **Google**.
- **Firestore Database:** 
  - Create the firestore database. 
  - Go to the **Rules** tab and paste the exact security rules 

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
- **Cloud Messaging (FCM):** Enable it for push notifications.

### Step 4: Configure the Android App (`Constants.kt`)
This is the most crucial part. You need to link your API keys and the deployed backend URLs dynamically.

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

### Step 5: Backend Deployment (FCM & Cloudinary)
StudentHub relies on a Node.js backend to handle push notifications and Cloudinary media uploading.

1. Go to the backend repository: **[Student_Hub_Backend](https://github.com/namanhere23/Student_Hub_Backend)**.
2. **Deploy the backend** on a cloud hosting platform like Render, Heroku, or Vercel.
3. During deployment, make sure you configure the following Environment Variables (`.env`) in your hosting dashboard:
   - `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
   - `FIREBASE_PROJECT_ID`, `FIREBASE_CLIENT_EMAIL`, `FIREBASE_PRIVATE_KEY` *(which you get from Firebase -> Project Settings -> Service Accounts)*
4. Once deployed successfully, copy your live backend URL (e.g. `https://my-backend-app.onrender.com`).
5. **Paste the deployed URL** back into the Android app's `Constants.kt` file for the `BASE_URL` and `FCM_BASE_URL` values.

### Step 6: Sync and Run the App
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