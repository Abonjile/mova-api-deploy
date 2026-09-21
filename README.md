# MOVA — Fitness Tracking Application

<!-- Team intro (will be added later) -->
<!-- Architecture diagram (will be added later) -->
<!-- Build instructions (will be added later) -->

---

## Table of Contents

- [Member 1 — Alwande Gwala: Authentication, Profile & Settings](#member-1--authentication-profile--settings)
- [Member 3 — Njabulo Mhlangu: Progress & Gamification](#member-3--progress--gamification)

---

## Member 1 — Authentication, Profile & Settings

### Overview
Responsible for user authentication, session persistence, profile management,
and app settings.

### Features Delivered

#### 1. Authentication
- **Register** — new users sign up with email + password. Passwords hashed
  with bcrypt on the backend.
- **Login** — existing users sign in; user ID persisted across restarts.
- **SSO (partial)** — Google/Apple sign-in UI present; full SDK integration
  planned for the final POE.

#### 2. Session Management
- Persistent session via **DataStore Preferences**.
- App restarts skip authentication if a session is active.
- Logout clears all session keys and returns to login.

#### 3. Post-Registration Onboarding
- New users land on an onboarding screen to capture height, weight, age,
  gender, fitness level, and fitness goals.
- Data is PUT to `/api/users/:userId` and used to personalise workouts.

#### 4. Profile Management
- View profile: name, avatar (initials), height, weight, age, fitness level.
- Edit Profile: all fields editable with validation.
- Fitness Objectives: user can add/remove multiple short-term objectives
  (stored as an array on the backend).

#### 5. Settings
- Dark mode toggle (persisted, applied app-wide)
- Larger text toggle
- High contrast toggle
- Biometric authentication toggle (persisted)
- Multi-language selection: English, isiZulu, Sesotho
- All persisted via `PUT /api/users/:userId/settings`

### Technical Highlights
- **MVVM architecture** with Hilt DI
- **Offline-first** — RoomDB caches user data; repository prefers local first
- **Retrofit + Gson** for REST API
- **DataStore** for session persistence
- **StateFlow** for reactive UI state

## Member 3 — Njabulo Mhlangu: Progress & Gamification

### Overview
Responsible for the Progress & Gamification module — the fitness tracking
dashboard experience that lets users join challenges, build streaks, earn
badges, and view personal analytics. Bridges Jetpack Compose UI with
Node.js / Express backend services.

### Features Delivered

### 1. Challenges
- Browse active fitness challenges (7, 10, and 30-day durations).
- Join a challenge with a single tap; user enrollment persisted to backend.
- Track progress: current day, completion percentage, streak count.
- Custom notes attached to daily challenge milestones.

### 2. Rewards
- Unlock badges on milestone completion.
- Track streaks and achievement history.
- Badge catalogue viewable from the Rewards tab.

### 3. Analytics
-  dashboard showing user fitness consistency.
- Performance graphs and workout history insights.
- Milestone tracking visualised for the user.

### Unit Tests
| Test class | Coverage |
|---|---|
| `AuthViewModelTest` | register success/failure, login success/missing userId, reset |
| `UserViewModelTest` | load from session, no-session error, add/remove objective, logout |

Run with:
```bash
./gradlew testDebugUnitTest

android.enableJetifier=true

Backend Integration: Ensure the environment variables (MongoDB connection string, port configurations) are properly configured in the .env file before launching the Node.js server
```


<!-- References -->
<!-- Team-wide references (will be added later) -->
