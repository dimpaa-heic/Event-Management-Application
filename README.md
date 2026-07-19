# 📅 Event Management Application
An Android mobile application that helps students discover, register, and manage campus events organized by student organizations at President University.

> **Mobile Programming Final Project** — Group 5, Semester 4 (2025)

---

## 👥 Team Members
Dian Theresia Girsang
Diva Clara Rosiana Marpaung
Nadira Putri Khumaira
Rachma Nur Aulia Fitri
Wa Ode Munandar Auliyah Wulandari

---

## 📱 About the App
This application provides a centralized platform for campus event information. Students can browse events from various organizations, register their attendance, and manage the events they have joined — all in one place.

---

## ✨ Features
- **Authentication** — Login and Register with Firebase Authentication
- **Home Event** — Browse all available campus events with search and filter by status (Ongoing / Finished)
- **Event Detail** — View full event information including poster, date, location, organizer, and category
- **Join Event** — Register attendance; event automatically saved to My Events
- **External Registration** — Open official registration link (Google Form) via browser
- **My Events** — View all events the user has joined
- **Organization Events** — Browse events filtered by student organization (PUMA, PUFA) and user's major
- **Profile** — View personal user information (Name, Email, NIM, Major)
- **Splash Screen** — Animated app entry screen

---

## 🛠️ Tech Stack
| Component | Technology |
|-----------|------------|
| Language | Java |
| IDE | Android Studio |
| Minimum SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Authentication | Firebase Authentication |
| Database | Firebase Firestore |
| Storage | Firebase Storage |
| Image Loading | Glide 4.16.0 |
| UI Components | View Binding, CircleImageView, Material Design |

---

## 🗄️ Database Structure (Firestore)
```
events/
└── eventId
    ├── title
    ├── description
    ├── date
    ├── time
    ├── location
    ├── posterUrl
    ├── kategory
    ├── registerUrl
    └── organizerName

users/
└── userId
    ├── name
    ├── email
    ├── nim
    └── major

registrations/
└── registrationId
    ├── userId
    ├── eventId
    └── joinedAt
```

---

## 📂 Project Structure
```
app/src/main/java/com/example/groupfive/
├── LoginActivity.java
├── RegisterActivity.java
├── SplashActivity.java
├── MainActivity.java
├── HomeActivity.java
├── EventDetailActivity.java
├── RegisterEventActivity.java
├── MyEventActivity.java
├── OrganizationActivity.java
├── ProfileActivity.java
├── EventAdapter.java
├── MyEventAdapter.java
├── Event.java
├── models/
│   └── EventModel.java
└── helpers/
    └── FirestoreHelper.java
```

---

## 🚀 How to Run
1. Clone this repository
   ```bash
   git clone https://github.com/your-username/event-management-app.git
   ```
2. Open the project in **Android Studio**
3. Set up Firebase:
   - Create a project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name `com.example.groupfive`
   - Download `google-services.json` and place it in the `app/` directory
   - Enable **Authentication** (Email/Password) and **Firestore Database** in Firebase Console
4. Sync Gradle and run the app on an emulator or physical device (Android 7.0+)

## 📄 License
This project is for **educational purposes only** and is not intended for commercial use.
