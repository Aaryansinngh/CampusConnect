# 📱 CampusConnect - University Club & Community Hub

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin%201.9-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/Design-Material%20Design%203-blue?style=flat&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean%20Architecture-orange?style=flat)](#-architecture)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**CampusConnect** is a modern, native Android application engineered for university ecosystems. It connects students, student club leaders, and campus administrators into a unified digital experience. Built using **Kotlin**, **MVVM Architecture**, **Material Design 3**, and **Firebase**, CampusConnect features an instant **dual-layer seed/offline mock data engine** that operates seamlessly out-of-the-box without requiring live cloud backend configuration.

---

## 📸 Screenshots

| Splash & Login | Home Feed | Club Discovery |
| :---: | :---: | :---: |
| ![Splash & Login](https://via.placeholder.com/280x560.png?text=Login+%26+Demo+Switcher) | ![Home Feed](https://via.placeholder.com/280x560.png?text=Home+Feed+%26+Announcements) | ![Club Discovery](https://via.placeholder.com/280x560.png?text=Explore+Clubs+%26+Categories) |

| Event Details | Admin Dashboard | User Profile |
| :---: | :---: | :---: |
| ![Event Details](https://via.placeholder.com/280x560.png?text=Event+Details+%26+RSVP) | ![Admin Dashboard](https://via.placeholder.com/280x560.png?text=Club+Admin+Event+Creation) | ![User Profile](https://via.placeholder.com/280x560.png?text=Student+Profile+%26+Passes) |

---

## 🌟 Key Features

### 🎓 Student Features
- **Club Discovery:** Browse campus clubs categorized by Technical, Cultural, Sports, Arts, and Entrepreneurship with keyword search and club profile view.
- **Interactive Feed:** Scroll through club updates, official announcements, liked posts, comments, and save favorite posts for offline reference.
- **Event Management & RSVP:** Discover upcoming hackathons, workshops, and fests with real-time seat availability tracking and one-tap registration.
- **Passes & Bookmarks:** Access registered events and saved announcements anytime from the user profile.
- **Instant Notifications:** Real-time feedback for successful registrations, new club announcements, and interaction updates.

### 🛡️ Club Admin Features
- **Publish Announcements & Posts:** Create rich feed posts or highlighted official announcements.
- **Event Scheduling:** Configure event titles, descriptions, dates, venues, deadlines, and seat capacity limits.
- **Attendee Tracking & Export:** Monitor real-time student registration counts and export attendee rosters directly to CSV format.

### 👑 Super Admin Governance
- **Role Control:** Grant or revoke `STUDENT`, `CLUB_ADMIN`, and `SUPER_ADMIN` privileges.
- **Campus Moderation:** Content oversight and verification approvals for new student clubs.

---

## ⚡ Quick Demo Mode (Zero Setup Required)

CampusConnect includes a **Demo Switcher** on the login screen, allowing testing across all role tiers without configuring Firebase:

| Role | Pre-Filled Demo Email | Password | Access Privileges |
| :--- | :--- | :--- | :--- |
| **Student** | `student@muj.manipal.edu` | `password123` | Feed, Club Directory, Event RSVP, Saved Posts |
| **Club Admin** | `admin@codingclub.com` | `password123` | Student Features + Post/Event Creation + Attendee CSV Export |
| **Super Admin** | `super@campusconnect.com` | `password123` | Full Access + Role Management & Campus Governance |

> **Note:** When `google-services.json` is not present, the app automatically switches to the built-in mock data engine.

---

## 🏗️ Architecture

CampusConnect follows the **MVVM (Model-View-ViewModel)** architectural pattern recommended by Google, combined with the **Repository Pattern**:

```
┌─────────────────────────────────────────────────────────────┐
│                         View Layer                          │
│          (Activities, Fragments, XML, ViewBinding)          │
└──────────────────────────────▲──────────────────────────────┘
                               │ Observes LiveData
┌──────────────────────────────┴──────────────────────────────┐
│                      ViewModel Layer                        │
│            (AuthViewModel, HomeViewModel, etc.)             │
└──────────────────────────────▲──────────────────────────────┘
                               │ Requests Data (Coroutines)
┌──────────────────────────────┴──────────────────────────────┐
│                      Repository Layer                       │
│              (AuthRepository, CampusRepository)             │
└──────────────▲───────────────────────────────▲──────────────┘
               │                               │
       (Live Firebase)                (Offline Fallback)
┌──────────────┴──────────────┐ ┌──────────────┴──────────────┐
│  Cloud Firestore / Auth     │ │     SampleDataGenerator     │
└─────────────────────────────┘ └─────────────────────────────┘
```

- **Separation of Concerns:** View logic is decoupled from data and business rules.
- **ViewBinding:** Eliminates `findViewById` boilerplate and guarantees compile-time null-safety.
- **Resource Wrapper:** Data states are propagated via `Resource<T>` (`Success`, `Error`, `Loading`).
- **Single-Activity Architecture:** Navigation handled via AndroidX Navigation Component and `NavHostFragment`.

---

## 🛠️ Technology Stack

| Category | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Language** | [Kotlin](https://kotlinlang.org/) | 1.9.22 | Primary application language |
| **SDK Targets** | Android SDK | Min: 24, Target: 34, Compile: 34 | Modern Android OS compatibility |
| **UI Components** | [Material Design 3](https://m3.material.io/) | 1.11.0 | Google M3 UI theming and controls |
| **Architecture** | Android Jetpack | Lifecycle 2.7.0, Navigation 2.7.7 | MVVM, LiveData, ViewModel, Navigation |
| **Concurrency** | Kotlin Coroutines | 1.7.3 | Asynchronous background processing |
| **Image Loading** | [Glide](https://github.com/bumptech/glide) | 4.16.0 | Image caching and thumbnail loading |
| **Cloud Services** | Firebase (BOM) | 32.7.2 | Authentication, Cloud Firestore, Storage |
| **Build System** | Gradle (Kotlin DSL) | 8.2.2 / Gradle 8.2 | Dependency and build management |

---

## 📁 Project Structure

```
CampusConnect/
├── app/
│   ├── build.gradle.kts                   # App-level build configuration & dependencies
│   ├── proguard-rules.pro                 # Proguard obfuscation rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml        # Application manifest & permissions
│           ├── java/com/campusconnect/app/
│           │   ├── CampusConnectApp.kt    # Application entry point
│           │   ├── adapter/               # RecyclerView adapters (Posts, Clubs, Events, etc.)
│           │   ├── data/
│           │   │   ├── model/             # Domain entities (User, Club, Post, Event, etc.)
│           │   │   └── repository/        # Data sources (AuthRepository, CampusRepository)
│           │   ├── ui/                    # UI screens & controllers
│           │   │   ├── MainActivity.kt    # Root single activity container
│           │   │   ├── admin/             # Club admin management screens
│           │   │   ├── auth/              # Login, Registration, Splash screens
│           │   │   ├── events/            # Event listing and RSVP detail screens
│           │   │   ├── explore/           # Club discovery & profile screens
│           │   │   ├── home/              # Main feed & announcements
│           │   │   ├── notifications/     # User notifications list
│           │   │   ├── posts/             # Post discussion detail screen
│           │   │   ├── profile/           # User profile & saved items
│           │   │   └── superadmin/        # System governance dashboard
│           │   ├── utils/                 # Resource wrapper, SessionManager, SampleDataGenerator
│           │   └── viewmodel/             # Jetpack ViewModels & ViewModelFactory
│           └── res/
│               ├── drawable/              # Vector drawables & background shapes
│               ├── layout/                # Material Design XML layouts
│               ├── menu/                  # Bottom navigation menu definition
│               ├── navigation/            # AndroidX navigation graph (nav_graph.xml)
│               └── values/                # Colors, strings, themes (M3 Day/Night)
├── build.gradle.kts                       # Root project build configuration
├── gradle.properties                      # JVM memory and build settings
├── settings.gradle.kts                    # Project modules and plugin repositories
├── firestore.rules                        # Cloud Firestore security rules
├── storage.rules                          # Cloud Storage security rules
├── .gitignore                             # Git ignore rules for Android artifacts & secrets
└── README.md                              # Project documentation
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Android Studio:** Hedgehog (2023.1.1) or newer recommended.
- **Java Development Kit (JDK):** Version 17 (bundled with modern Android Studio).
- **Android SDK:** API Level 34 platform & build-tools installed.
- **Device / Emulator:** Android 7.0 (API Level 24) or higher.

### Step-by-Step Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/CampusConnect.git
   cd CampusConnect
   ```

2. **Open in Android Studio:**
   - Launch Android Studio.
   - Select **Open** and choose the `CampusConnect` root folder.
   - Wait for Gradle to finish indexing and syncing dependencies.

3. **Run on an Emulator or Physical Device:**
   - Select a target device (e.g. Pixel 7 with API 34).
   - Click the green **Run** button (`Shift + F10`) or execute:
     ```bash
     .\gradlew.bat installDebug
     ```

---

## 🔨 Build Instructions

To build the project from the command line:

### Debug Build
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```
The resulting APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Clean & Rebuild
```bash
.\gradlew.bat clean assembleDebug
```

---

## 🔥 Optional: Connecting Live Firebase

If you want to transition from the offline mock engine to live Firebase services:

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Add an Android app with package name:
   ```
   com.campusconnect.app
   ```
3. Enable:
   - **Authentication** (Email/Password provider)
   - **Cloud Firestore** (Deploy the rules provided in `firestore.rules`)
   - **Cloud Storage** (Deploy the rules provided in `storage.rules`)
4. Download `google-services.json` and place it in the `app/` folder:
   ```
   CampusConnect/app/google-services.json
   ```
5. Rebuild the app. The Gradle build script will automatically detect the file and enable the Google Services plugin.

---

## 📄 License & Attribution

Distributed under the MIT License. See `LICENSE` for more information.

Developed with ❤️ for university student communities.
